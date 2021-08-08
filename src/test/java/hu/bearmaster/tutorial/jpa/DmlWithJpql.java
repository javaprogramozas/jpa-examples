package hu.bearmaster.tutorial.jpa;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.User;
import hu.bearmaster.tutorial.jpa.model.UserStatus;

class DmlWithJpql {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    @Test
    void simpleUpdate() {
        // Update posts with low number of likes
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery("UPDATE Post p SET p.likes = 10 WHERE p.likes < 10").executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    @Test
    void simpleUpdateWithParameters() {
        // Update posts in topic 'Vicces'
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery("UPDATE Post p SET p.likes = :newLikes WHERE p.topic = :topic")
                .setParameter("topic", "Vicces")
                .setParameter("newLikes", 15)
                .executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    @Test
    void updateRelation() {
        // Update posts without author
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        String updateSql = """
                UPDATE Post p 
                SET p.author = (SELECT u 
                                FROM User u 
                                WHERE u.id = :userId) 
                WHERE p.author IS NULL
                """;
        int affectedRows = entityManager.createQuery(updateSql)
                .setParameter("userId", 1L)
                .executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    @Test
    void updateRelationUsingEntityParameter() {
        // Update posts without author
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        User user = entityManager.find(User.class, 1L);
        int affectedRows = entityManager.createQuery("UPDATE Post p SET p.author = :user WHERE p.author IS NULL")
                .setParameter("user", user)
                .executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    @Test
    void updateCollectionShouldNotWork() {
        // Update user's roles
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery("UPDATE User u SET u.roles = :roles")
                .setParameter("roles", Set.of("USER"))
                .executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    @Test
    void traverseRelation() {
        // Deactivate users without posts
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery("UPDATE User u SET u.status = 'INACTIVE' WHERE u.posts IS EMPTY").executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    @Test
    void simpleDelete() {
        // Delete inactive users
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery("DELETE User u WHERE u.status = 'INACTIVE'").executeUpdate();
        
        System.out.println("Removed " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    @Test
    void namedQuery() {        
        List<Post> posts = entityManager.createNamedQuery("selectPostsByLikesAndTitle", Post.class)
            .setParameter("likes", 10)
            .setParameter("word", "méz")
            .getResultList();
        
        System.out.println(posts);
    }
    
    @Test
    void nativeQueryWithMapping() {        
        String query = """
                SELECT u.id, u.username, u.status, u.created_at
                FROM blogs.users u
                WHERE lower(u.username) = reverse(lower(u.username)) 
                AND u.status = :status
                """;
        List<User> palindromUsers = entityManager.createNativeQuery(query, User.class)
                .setParameter("status", UserStatus.ACTIVE.name())
                .getResultList();
        
        System.out.println(palindromUsers);
    }
    
    @Test
    void nativeQuery() {        
        String query = """
                SELECT u.id, u.username, u.status, u.created_at
                FROM blogs.users u
                WHERE lower(u.username) = reverse(lower(u.username)) 
                AND u.status = :status
                """;
        List<Object[]> users = entityManager.createNativeQuery(query)
                .setParameter("status", UserStatus.ACTIVE.name())
                .getResultList();
        
        users.forEach(array -> System.out.println(Arrays.toString(array)));
    }
    
    @Test
    void namedNativeQuery() {        
        List<User> users = entityManager.createNamedQuery("usersWithPalindromName", User.class)
                .getResultList();
        
        System.out.println(users);
    }

}
