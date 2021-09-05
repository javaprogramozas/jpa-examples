package hu.bearmaster.tutorial.jpa;

import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.TitleAndLike;

class SimpleCriteriaQueries {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();

    @Test
    void basicQuery() {
        // FROM Post
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        query.from(Post.class);
        
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void basicQueryWithSelect() {
        // SELECT p FROM Post p
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        query.select(post);
        
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void basicQueryWithProjectionInSelect() {
        // SELECT p.title FROM Post p
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OffsetDateTime> query = builder.createQuery(OffsetDateTime.class);
        Root<Post> post = query.from(Post.class);
        query.select(post.get("createdOn"));
        
        List<OffsetDateTime> titles = entityManager.createQuery(query).getResultList();
        
        System.out.println(titles.get(0));
    }
    
    @Test
    void queryWithWhere() {
        // SELECT p FROM Post p WHERE p.likes > 10 AND p.title LIKE '%méz%'
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        query.select(post);
        query.where(builder.and(
                builder.greaterThan(post.get("likes"), 10), 
                builder.like(post.get("title"), "%méz%")));
        
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithWhereOr() {
        // SELECT p FROM Post p WHERE p.likes > 10 OR p.title LIKE '%méz%'
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        query.select(post);
        query.where(builder.or(
                builder.greaterThan(post.get("likes"), 10), 
                builder.like(post.get("title"), "%méz%")));
        
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithOrderBy() {
        // SELECT p FROM Post p ORDER BY p.likes DESC
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        query.select(post);
        query.orderBy(builder.desc(post.get("likes")), builder.asc(post.get("id")));
        
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithAggregateFunctions() {
        // SELECT MAX(p.likes) FROM Post p
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
        Root<Post> post = query.from(Post.class);
        query.select(builder.max(post.get("likes")));
        
        Integer maxLikes = entityManager.createQuery(query).getSingleResult();
        
        System.out.println("Maximum like number: " + maxLikes);
    }
    
    // -------------------------------------------
    
    @Test
    void queryWithNamedParameters() {
        // SELECT p FROM Post p WHERE p.likes > :likes AND LOCATE(:word, p.title) > 0
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        query.select(post);
        ParameterExpression<Integer> likesParameter = builder.parameter(Integer.class);
        ParameterExpression<String> titleParameter = builder.parameter(String.class);
        
        query.where(
                builder.greaterThan(post.get("likes"), likesParameter),
                builder.greaterThan(builder.locate(post.get("title"), titleParameter), 0));
        
        List<Post> posts = entityManager.createQuery(query)
                .setParameter(likesParameter, 10)
                .setParameter(titleParameter, "méz")
                .getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithReusedParameters() {
        // SELECT p FROM Post p WHERE LOCATE(:word, p.description) > 0 AND LOCATE(:word, p.title) > 0
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> root = query.from(Post.class);
        query.select(root);
        ParameterExpression<String> wordParameter = builder.parameter(String.class);
        query.where(
                builder.greaterThan(builder.locate(root.get("title"), wordParameter), 0),
                builder.greaterThan(builder.locate(root.get("description"), wordParameter), 0));
        
        List<Post> posts = entityManager.createQuery(query)
                .setParameter(wordParameter, "Az")
                .getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithCustomReturnType() {
        // SELECT p.title, p.likes FROM Post p
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        Root<Post> post = query.from(Post.class);
        //query.select(builder.array(post.get("title"), post.get("likes")));
        query.multiselect(post.get("title"), post.get("likes"));
        
        List<Object[]> posts = entityManager.createQuery(query).getResultList();
        
        posts.forEach(array -> System.out.println("'" + array[0] + "' bejegyzésnek " + array[1] + " like-ja van"));
    }
    
    @Test
    void queryWithCustomReturnTypeInEnclosingClass() {
        // SELECT new hu.bearmaster.tutorial.jpa.model.TitleAndLike(p.title, p.likes) FROM Post p
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TitleAndLike> query = builder.createQuery(TitleAndLike.class);
        Root<Post> post = query.from(Post.class);
        Path<Object> title = post.get("title");
        Path<Object> likes = post.get("likes");
        
        query.select(builder.construct(TitleAndLike.class, title, likes));
        
        List<TitleAndLike> titleAndLikeList = entityManager.createQuery(query).getResultList();
        
        System.out.println(titleAndLikeList);
    }
    
    @Test
    void queryWithTuple() {
        // SELECT p.title, p.likes FROM Post p
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<Post> post = query.from(Post.class);
        Path<String> title = post.get("title");
        Path<Integer> likes = post.get("likes");
        
        query.multiselect(title.alias("title"), likes.alias("likes"));
        
        List<Tuple> tuples = entityManager.createQuery(query).getResultList();
        
        for (Tuple tuple : tuples) {
            //System.out.println("'" + tuple.get(0) + "' bejegyzésnek " + tuple.get(1) + " like-ja van");
            //System.out.println("'" + tuple.get("title") + "' bejegyzésnek " + tuple.get("likes") + " like-ja van");
            System.out.println("'" + tuple.get(title) + "' bejegyzésnek " + tuple.get(likes) + " like-ja van");
        }
    }
    
    @Test
    void queryWithGroupByAndHaving() {
        // SELECT p.topic, SUM(p.likes) FROM Post p GROUP BY p.topic HAVING LENGTH(p.topic) > 5
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);
        Root<Post> post = query.from(Post.class);
        Path<String> title = post.get("topic");
        Expression<Integer> sumLikes = builder.sum(post.get("likes"));
        query.multiselect(title, sumLikes);
        
        query.groupBy(title);
        query.having(builder.gt(builder.length(title), 5));
        
        List<Tuple> results = entityManager.createQuery(query).getResultList();
        
        for (Tuple tuple : results) {
            System.out.println("'" + tuple.get(0) + "' témának összesen " + tuple.get(1) + " like-ja van");
        }
    }
    
    
    
}
