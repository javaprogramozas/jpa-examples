package hu.bearmaster.tutorial.jpa;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.TitleAndLike;
import hu.bearmaster.tutorial.jpa.model.User;

class SimpleJpqlQueries {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();

    @Test
    void basicQuery() {
        List<Post> posts = entityManager.createQuery("FROM Post", Post.class).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void basicQueryWithIdentifier() {
        String query = """
                FROM Post p
                """;
        List<Post> posts = entityManager.createQuery(query, Post.class).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void basicQueryWithSelect() {
        String query = """
                SELECT p.createdOn
                FROM Post p
                """;
        List<ZonedDateTime> titles = entityManager.createQuery(query, ZonedDateTime.class).getResultList();
        
        System.out.println(titles);
    }
    
    @Test
    void queryWithWhereAndOrderBy() {
        String query = """
                SELECT p
                FROM Post p
                WHERE p.likes > 10
                AND p.title LIKE '%méz%'
                ORDER BY p.id DESC
                """;
        List<Post> posts = entityManager.createQuery(query, Post.class).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithAggregateFunctions() {
        String query = """
                SELECT MAX(p.likes)
                FROM Post p
                """;
        
        Integer maxLikes = entityManager.createQuery(query, Integer.class).getSingleResult();
        
        System.out.println("Maximum like number: " + maxLikes);
    }
    
    // -------------------------------------------
    
    @Test
    void queryWithNamedParameters() {
        String query = """
                SELECT p
                FROM Post p
                WHERE p.likes > :likes
                AND LOCATE(:word, p.title) > 0 
                ORDER BY p.id DESC
                """;
        List<Post> posts = entityManager.createQuery(query, Post.class)
                .setParameter("likes", 10)
                .setParameter("word", "méz")
                .getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithPositionalParameters() {
        String query = """
                SELECT p
                FROM Post p
                WHERE p.likes > ?1
                AND LOCATE(?2, p.title) > 0 
                ORDER BY p.id DESC
                """;
        List<Post> posts = entityManager.createQuery(query, Post.class)
                .setParameter(1, 10)
                .setParameter(2, "méz")
                .getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithReusedParameters() {
        String query = """
                SELECT p
                FROM Post p
                WHERE LOCATE(:word, p.description) > 0
                AND LOCATE(:word, p.title) > 0 
                ORDER BY p.id DESC
                """;
        List<Post> posts = entityManager.createQuery(query, Post.class)
                .setParameter("word", "Az")
                .getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithCustomReturnType() {
        String query = """
                SELECT p.title, p.likes
                FROM Post p
                """;
        List<Object[]> posts = entityManager.createQuery(query, Object[].class).getResultList();
        
        posts.forEach(array -> System.out.println("'" + array[0] + "' bejegyzésnek " + array[1] + " like-ja van"));
    }
    
    @Test
    void queryWithCustomReturnTypeInEnclosingClass() {
        String query = """
                SELECT new hu.bearmaster.tutorial.jpa.model.TitleAndLike(p.title, p.likes)
                FROM Post p
                """;
        List<TitleAndLike> posts = entityManager.createQuery(query, TitleAndLike.class).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithGroupByAndHaving() {
        String query = """
                SELECT p.topic, SUM(p.likes)
                FROM Post p
                GROUP BY p.topic
                HAVING LENGTH(p.topic) > 5
                """;
        List<Object[]> results = entityManager.createQuery(query, Object[].class).getResultList();
        
        results.forEach(array -> System.out.println(array[0] + ": " + array[1]));
    }
    
    
    
}
