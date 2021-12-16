package hu.bearmaster.tutorial.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.Post_;
import hu.bearmaster.tutorial.jpa.model.User;
import hu.bearmaster.tutorial.jpa.model.UserStatus;
import hu.bearmaster.tutorial.jpa.model.User_;

class CriteriaQueriesUsingMetamodel {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    @Test
    void queryWithWhere() {
        // SELECT p FROM Post p WHERE p.likes > 10 AND p.title LIKE '%méz%'
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        Path<Long> idPath = post.get(Post_.id);
        query.select(post);
        query.where(builder.and(
                builder.greaterThan(post.get(Post_.likes), 10), 
                builder.like(post.get(Post_.title), "%méz%")));
        
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithTuple() {
        // SELECT p.title, p.likes FROM Post p
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<Post> post = query.from(Post.class);
        Path<String> title = post.get(Post_.title);
        Path<Integer> likes = post.get(Post_.likes);
        
        query.multiselect(title.alias("title"), likes.alias("likes"));
        
        List<Tuple> tuples = entityManager.createQuery(query).getResultList();
        
        for (Tuple tuple : tuples) {
            //System.out.println("'" + tuple.get(0) + "' bejegyzésnek " + tuple.get(1) + " like-ja van");
            //System.out.println("'" + tuple.get("title") + "' bejegyzésnek " + tuple.get("likes") + " like-ja van");
            System.out.println("'" + tuple.get(title) + "' bejegyzésnek " + tuple.get(likes) + " like-ja van");
        }
    }
    
    @Test
    void fetchJoinWithReference() {
        // Posts with 5+ likes and active authors
        // SELECT p FROM Post p JOIN FETCH p.author u WHERE p.likes > 5 AND u.status = 'ACTIVE'
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        Fetch<Post, User> fetch = post.fetch(Post_.author);
        
        query.select(post);
        query.where(builder.gt(post.get("likes"), 5), builder.equal(post.get(Post_.author).get(User_.status), UserStatus.ACTIVE));
        
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        for (Post p : posts) {
            System.out.println(p.getTitle() + " from " + p.getAuthor().getUsername());
        }
    }
    
    @Test
    void subQueriesWithAll() {
        // Users with all their posts in the same topic
        // SELECT DISTINCT u FROM User u JOIN u.posts p WHERE p.topic = ALL (SELECT p.topic FROM u.posts p)
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> user = query.from(User.class);
        SetJoin<User, Post> post = user.join(User_.posts);
        
        Subquery<String> subquery = query.subquery(String.class);
        Root<Post> subPost = subquery.from(Post.class);
        subquery.select(subPost.get("topic"));
        subquery.where(builder.equal(subPost.get(Post_.author), user));
        
        query.select(user);
        query.distinct(true);
        query.where(builder.equal(post.get(Post_.topic), builder.all(subquery)));
        
        List<User> users = entityManager.createQuery(query).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void simpleUpdateWithParameters() {
        // Update posts in topic 'Vicces'
        // UPDATE Post p SET p.likes = :newLikes WHERE p.topic = :topic
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Post> update = builder.createCriteriaUpdate(Post.class);
        Root<Post> post = update.from(Post.class);
        
        ParameterExpression<Integer> likesParameter = builder.parameter(Integer.class, "majom");
        ParameterExpression<String> topicParameter = builder.parameter(String.class, "ketrec");
        
        update.set(post.get(Post_.likes), likesParameter);
        update.where(builder.equal(post.get("topic"), topicParameter));
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery(update)
                .setParameter(likesParameter, 10)
                .setParameter(topicParameter, "Vicces")
                .executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
    }

}
