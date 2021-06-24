package hu.bearmaster.tutorial.jpa.dao;

import javax.persistence.EntityManager;

import hu.bearmaster.tutorial.jpa.model.Address;

public class AddressDao extends AbstractDao<Address, Long> {
    
    public AddressDao() {
        super("blogs-pu");
    }
    
    public Address getAddressById(Long id) {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        return entityManager.find(Address.class, id);
    }
    
    public Address create(Address address) {
        return runInTransaction(entityManager -> {
            entityManager.persist(address);
            return address;
        });
    }
    
    public Address update(Address address) {
        return runInTransaction(entityManager -> entityManager.merge(address));
    }
    
    public void remove(Address address) {
        runInTransaction(entityManager -> {
            Address reference = entityManager.find(Address.class, address.getId());
            entityManager.remove(reference);
            return null;
        });
    }

}
