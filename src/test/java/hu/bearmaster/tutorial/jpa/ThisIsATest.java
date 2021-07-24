package hu.bearmaster.tutorial.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Test;

public class ThisIsATest {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    @Test
    void lol() {
        String userQuery = """
                SELECT u
                FROM User u
                WHERE u.address.street = 'Kossuth'
                """;
        List users = entityManager.createQuery(userQuery).getResultList();
        
        System.out.println(users);
    }

}
