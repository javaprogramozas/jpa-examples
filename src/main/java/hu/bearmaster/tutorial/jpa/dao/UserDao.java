package hu.bearmaster.tutorial.jpa.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import hu.bearmaster.tutorial.jpa.model.User;

//Data Access Object
public class UserDao {
    
    private EntityManagerFactory emf;
    
    public UserDao() {
        this.emf = Persistence.createEntityManagerFactory("blogs-pu");
    }
    
    public User getUserById(Long id) {
        EntityManager entityManager = emf.createEntityManager();
        return entityManager.find(User.class, id);
    }
    
    public User create(User user) {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        
        try {
            entityManager = emf.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
        } catch (Exception e) {
            if ( transaction != null && transaction.isActive() ) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if ( entityManager != null ) {
                entityManager.close();
            }
        }
        
        return user;
    }
    
    public User update(User user) {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        
        try {
            entityManager = emf.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if ( transaction != null && transaction.isActive() ) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if ( entityManager != null ) {
                entityManager.close();
            }
        }
        
        return user;
    }

}
