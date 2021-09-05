package hu.bearmaster.tutorial.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.User;

class EntityRelationshipJpql {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    @Test
    void traverseSingleRelationShipOneToMany() {
        // Posts with authors in PENDING status
        String query = """
                FROM Post p
                WHERE p.author.status = hu.bearmaster.tutorial.jpa.model.UserStatus.PENDING
                """;
        
        List<Post> posts = entityManager.createQuery(query, Post.class).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void traverseSingleRelationshipOneToOne() {
        // Users living on Rákóczi street
        String query = """
                SELECT u
                FROM User u
                WHERE u.address.street = 'Rákóczi'
                """;
        
        List<User> users = entityManager.createQuery(query, User.class).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void traverseSingleRelationshipsMultiLayer() {
        // Posts authored by users living on Rákóczi street
        String query = """
                FROM Post p
                WHERE p.author.address.street = 'Rákóczi'
                """;
        
        List<Post> posts = entityManager.createQuery(query, Post.class).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void traverseMultiRelationshipManyToOneUsingJoin() {
        // Users with posts more then 5 likes
        String query = """
                SELECT DISTINCT u
                FROM User u
                INNER JOIN u.posts p
                WHERE p.likes > 5
                """;
        
        List<User> users = entityManager.createQuery(query, User.class).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void traverseMultiRelationshipManyToOneUsingIn() {
        // Users with posts more then 5 likes
        String query = """
                SELECT DISTINCT u
                FROM User u, IN (u.posts) p
                WHERE p.likes > 5
                """;
        
        List<User> users = entityManager.createQuery(query, User.class).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void simpleJoin() {
        // Posts with 5+ likes and active authors
        String query = """
                SELECT p
                FROM Post p
                JOIN p.author u
                WHERE p.likes > 5
                AND u.status = hu.bearmaster.tutorial.jpa.model.UserStatus.ACTIVE
                """;
        
        List<Post> posts = entityManager.createQuery(query, Post.class).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void fetchJoin() {
       // Posts with 5+ likes and active authors
        String query = """
                SELECT p
                FROM Post p
                JOIN FETCH p.author u
                WHERE p.likes > 5
                AND u.status = hu.bearmaster.tutorial.jpa.model.UserStatus.ACTIVE
                """;
        
        List<Post> posts = entityManager.createQuery(query, Post.class).getResultList();
        
        for (Post post : posts) {
            System.out.println(post.getTitle() + " from " + post.getAuthor().getUsername());
        }
    }
    
    @Test
    void leftJoin() {
        // Posts with 5+ likes regardless having an author
        String query = """
                FROM Post p
                LEFT OUTER JOIN FETCH p.author u
                WHERE p.likes > 5
                """;
        
        List<Post> posts = entityManager.createQuery(query, Post.class).getResultList();
        
        for (Post post : posts) {
            System.out.println(post.getTitle() + " from " + Optional.ofNullable(post.getAuthor()).map(user -> user.getUsername()).orElse("n/a"));
        }
    }
    
    @Test
    void missingRelationFromOneSide() {
        // Posts without authors
        String query = """
                FROM Post p
                WHERE p.author IS NULL
                """;
        
        List<Post> posts = entityManager.createQuery(query, Post.class).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void missingRelationFromManySide() {
        // Authors without posts
        String query = """
                FROM User u
                WHERE u.posts IS EMPTY
                """;
        
        List<User> users = entityManager.createQuery(query, User.class).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void membershipOfRelation() {
        // Users with ADMIN role
        String query = """
                FROM User u
                WHERE 'ADMIN' MEMBER OF u.roles
                """;
        
        List<User> users = entityManager.createQuery(query, User.class).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void subQueries() {
        // Users with more than 3 posts
        String query = """
                FROM User u
                WHERE (SELECT COUNT(p) FROM u.posts p) > 3
                """;
        
        List<User> users = entityManager.createQuery(query, User.class).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void subQueriesWithExists() {
        // Users with posts in 'Vicces' topic
        String query = """
                FROM User u
                WHERE EXISTS (SELECT p FROM u.posts p WHERE p.topic = 'Vicces')
                """;
        
        List<User> users = entityManager.createQuery(query, User.class).getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void subQueriesWithAll() {
        // Users with all their posts in the same topic
        String query = """
                SELECT DISTINCT u
                FROM User u
                JOIN u.posts p
                WHERE p.topic = ALL (SELECT p.topic FROM u.posts p) 
                """;
        
        List<User> users = entityManager.createQuery(query, User.class).getResultList();
        
        System.out.println(users);
    }

}
