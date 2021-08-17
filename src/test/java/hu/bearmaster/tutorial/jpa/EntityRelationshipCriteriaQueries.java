package hu.bearmaster.tutorial.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Address;
import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.User;
import hu.bearmaster.tutorial.jpa.model.UserStatus;

class EntityRelationshipCriteriaQueries {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    @Test
    void traverseSingleRelationShipOneToMany() {
        // Posts with authors in PENDING status
        // FROM Post p WHERE p.author.status = hu.bearmaster.tutorial.jpa.model.UserStatus.PENDING
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        query.where(builder.equal(post.get("author").get("status"), UserStatus.PENDING));
        
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void traverseSingleRelationshipOneToOne() {
        // Users living on Rákóczi street
        // SELECT u FROM User u WHERE u.address.street = 'Rákóczi'
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> user = query.from(User.class);
        query.select(user);
        query.where(builder.equal(user.get("address").get("street"), "Rákóczi"));
        
        List<User> users = entityManager.createQuery(query).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void traverseMultiRelationshipManyToOneUsingJoin() {
        // Users with posts more then 5 likes
        // SELECT DISTINCT u FROM User u INNER JOIN u.posts p WHERE p.likes > 5
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> user = query.from(User.class);
        Join<User, Post> post = user.join("posts");
        query.select(user);
        query.distinct(true);
        
        query.where(builder.gt(post.get("likes"), 5));
        
        List<User> users = entityManager.createQuery(query).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void traverseSingleRelationshipsMultiLayer() {
        // Posts authored by users living on Rákóczi street
        // SELECT p FROM Post p WHERE p.author.address.street = 'Rákóczi'
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        
        Join<Post, User> user = post.join("author");
        Join<User, Address> address = user.join("address");
        
        query.select(post);
        query.where(builder.equal(address.get("street"), "Rákóczi"));
        
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void fetchJoinWithReference() {
        // Posts with 5+ likes and active authors
        // SELECT p FROM Post p JOIN FETCH p.author u WHERE p.likes > 5 AND u.status = 'ACTIVE'
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        post.fetch("author");
        
        query.select(post);
        query.where(builder.gt(post.get("likes"), 5), builder.equal(post.get("author").get("status"), UserStatus.ACTIVE));
        
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        for (Post p : posts) {
            System.out.println(p.getTitle() + " from " + p.getAuthor().getUsername());
        }
    }
    
    @Test
    void fetchJoinWithDynamicEntityGraph() {
        // Posts with 5+ likes and active authors
        // SELECT p FROM Post p JOIN FETCH p.author u WHERE p.likes > 5 AND u.status = 'ACTIVE'
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        Join<Post, User> author = post.join("author");
        EntityGraph<Post> fetchGraph = entityManager.createEntityGraph(Post.class);
        fetchGraph.addSubgraph("author", User.class);
        
        query.select(post);
        query.where(builder.gt(post.get("likes"), 5), builder.equal(author.get("status"), UserStatus.ACTIVE));
        
        List<Post> posts = entityManager.createQuery(query)
                .setHint("javax.persistence.loadgraph", fetchGraph)
                .getResultList();
        
        for (Post p : posts) {
            System.out.println(p.getTitle() + " from " + p.getAuthor().getUsername());
        }
    }
    
    @Test
    void leftJoin() {
        // Posts with 5+ likes regardless having an author
        // FROM Post p LEFT OUTER JOIN FETCH p.author u WHERE p.likes > 5
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        post.join("author", JoinType.LEFT);
        
        query.where(builder.gt(post.get("likes"), 5));
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        for (Post p : posts) {
            System.out.println(p.getTitle() + " from " + Optional.ofNullable(p.getAuthor()).map(user -> user.getUsername()).orElse("n/a"));
        }
    }
    
    // -----------------------------
    
    @Test
    void missingRelationFromOneSide() {
        // Posts without authors
        // FROM Post p WHERE p.author IS NULL
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        
        query.where(builder.isNull(post.get("author")));
        List<Post> posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void missingRelationFromManySide() {
        // Authors without posts
        // FROM User u WHERE u.posts IS EMPTY
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> user = query.from(User.class);
        
        query.where(builder.isEmpty(user.get("posts")));
        
        List<User> users = entityManager.createQuery(query).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void membershipOfRelation() {
        // Users with ADMIN role
        // FROM User u WHERE 'ADMIN' MEMBER OF u.roles
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> user = query.from(User.class);
        
        query.where(builder.isMember("ADMIN", user.get("roles")));
        
        List<User> users = entityManager.createQuery(query).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void subQueries() {
        // Users with more than 3 posts
        // FROM User u WHERE (SELECT COUNT(p) FROM u.posts p) > 3
        // SELECT * FROM users u WHERE (SELECT count(p.id) FROM posts p WHERE p.author_id = u.id) > 3
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> user = query.from(User.class);
        
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Post> post = subquery.from(Post.class);
        subquery.select(builder.count(post));
        subquery.where(builder.equal(post.get("author"), user));
        
        query.select(user);
        query.where(builder.gt(subquery, 3));
        
        List<User> users = entityManager.createQuery(query).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void subQueriesWithExists() {
        // Users with posts in 'Vicces' topic
        // FROM User u WHERE EXISTS (SELECT p FROM u.posts p WHERE p.topic = 'Vicces')
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> user = query.from(User.class);
        
        Subquery<Post> subquery = query.subquery(Post.class);
        Root<Post> post = subquery.from(Post.class);
        subquery.select(post);
        subquery.where(builder.equal(post.get("author"), user), builder.equal(post.get("topic"), "Vicces"));
        
        query.select(user);
        query.where(builder.exists(subquery));
        
        List<User> users = entityManager.createQuery(query).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void subQueriesWithAll() {
        // Users with all their posts in the same topic
        // SELECT DISTINCT u FROM User u JOIN u.posts p WHERE p.topic = ALL (SELECT p.topic FROM u.posts p)
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> user = query.from(User.class);
        Join<User, Post> post = user.join("posts");
        
        Subquery<String> subquery = query.subquery(String.class);
        Root<Post> subPost = subquery.from(Post.class);
        subquery.select(subPost.get("topic"));
        subquery.where(builder.equal(subPost.get("author"), user));
        
        query.select(user);
        query.distinct(true);
        query.where(builder.equal(post.get("topic"), builder.all(subquery)));
        
        List<User> users = entityManager.createQuery(query).getResultList();
        
        System.out.println(users);
    }

}
