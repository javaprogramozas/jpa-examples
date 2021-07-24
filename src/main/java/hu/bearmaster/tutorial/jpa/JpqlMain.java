package hu.bearmaster.tutorial.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.User;

public class JpqlMain {
    
    public static void main(String[] args) {
        
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        String postQuery = """
                SELECT p
                FROM Post p
                WHERE p.likes > 10
                ORDER BY p.likes DESC
                """;
        List<Post> posts = entityManager.createQuery(postQuery).getResultList();
        
        System.out.println(posts);
        
        
        
    }

}
