package hu.bearmaster.tutorial.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.User;
import hu.bearmaster.tutorial.jpa.model.UserStatus;

public class DmlWithCriteriaQueries {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    @Test
    void simpleUpdate() {
        // Update posts with low number of likes
        // UPDATE Post p SET p.likes = 10 WHERE p.likes < 10
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Post> update = builder.createCriteriaUpdate(Post.class);
        Root<Post> post = update.from(Post.class);
        update.set(post.get("likes"), 10);
        update.where(builder.lt(post.get("likes"), 10));
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery(update).executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
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
        
        Path<Integer> likes = post.get("likes");
        update.set(likes, likesParameter);
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
    
    @Test
    void updateRelation() {
        // Update posts without author
        // UPDATE Post p SET p.author = (SELECT u FROM User u WHERE u.id = :userId) WHERE p.author IS NULL
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Post> update = builder.createCriteriaUpdate(Post.class);
        
        Root<Post> post = update.from(Post.class);
        Path<User> author = post.get("author");
        
        Subquery<User> subquery = update.subquery(User.class);
        Root<User> user = subquery.from(User.class);
        subquery.select(user);
        subquery.where(builder.equal(user.get("id"), 1L));
        
        update.set(author, subquery);
        update.where(builder.isNull(author));
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery(update).executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    @Test
    void updateRelationUsingEntityParameter() {
        // Update posts without author
        // UPDATE Post p SET p.author = :user WHERE p.author IS NULL
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Post> update = builder.createCriteriaUpdate(Post.class);
        User user = entityManager.find(User.class, 1L);
        Root<Post> post = update.from(Post.class);
        Path<User> author = post.get("author");
        
        update.set(author, user);
        update.where(builder.isNull(author));
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery(update).executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    @Test
    void simpleDelete() {
        // Delete inactive users
        // DELETE User u WHERE u.status = 'INACTIVE'
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaDelete<User> delete = builder.createCriteriaDelete(User.class);
        Root<User> user = delete.from(User.class);
        delete.where(builder.equal(user.get("status"), UserStatus.INACTIVE));
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery(delete).executeUpdate();
        
        System.out.println("Removed " + affectedRows + " records");
        
        transaction.rollback();
    }
}
