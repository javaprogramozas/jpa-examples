package hu.bearmaster.tutorial.jpa.dao;

import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public abstract class AbstractDao<T, ID> {
    
    private final EntityManagerFactory emf;
    
    public AbstractDao(String persistentUnitName) {
        this(Persistence.createEntityManagerFactory(persistentUnitName));
    }
    
    public AbstractDao(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    protected EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
    
    protected abstract Class<T> getEntityClass();
    
    protected T getById(ID id) {
        EntityManager entityManager = emf.createEntityManager();
        return entityManager.find(getEntityClass(), id);
    }
    
    protected T runInTransaction(Function<EntityManager, T> function) {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        T result = null;
        
        try {
            entityManager = emf.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            result = function.apply(entityManager);
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
        
        return result;
    }

}
