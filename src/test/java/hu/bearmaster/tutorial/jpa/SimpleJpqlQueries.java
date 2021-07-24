package hu.bearmaster.tutorial.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Post;

public class SimpleJpqlQueries {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();

    @Test
    void basicQuery() {
        List<Post> posts = entityManager.createQuery("").getResultList();
        
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
                FROM Post p
                """;
        List titles = entityManager.createQuery(query).getResultList();
        
        System.out.println(titles);
    }
    
    @Test
    void queryWithWhereAndOrderBy() {
        String query = """
                SELECT p
                From Post p
                """;
        List<Post> posts = entityManager.createQuery(query, Post.class).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithAggregateFunctions() {
        String query = """
                SELECT p
                FROM Post p
                """;
        
        List posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithNamedParameters() {
        String query = """
                SELECT p
                FROM Post p
                """;
        List<Post> posts = entityManager.createQuery(query, Post.class)
                .getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithPositionalParameters() {
        String query = """
                SELECT p
                FROM Post p
                """;
        List<Post> posts = entityManager.createQuery(query, Post.class)
                .getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithReusedParameters() {
        String query = """
                SELECT p
                FROM Post p
                """;
        List<Post> posts = entityManager.createQuery(query, Post.class)
                .getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithCustomReturnType() {
        String query = """
                SELECT p
                FROM Post p
                """;
        List posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithCustomReturnTypeInEnclosingClass() {
        String query = """
                SELECT p.title, p.likes
                FROM Post p
                """;
        List posts = entityManager.createQuery(query).getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void queryWithGroupByAndHaving() {
        String query = """
                SELECT p
                FROM Post p
                """;
        List<Object[]> results = entityManager.createQuery(query, Object[].class).getResultList();
        
        results.forEach(array -> System.out.println(array[0] + ": " + array[1]));
    }
    
    
    
}
