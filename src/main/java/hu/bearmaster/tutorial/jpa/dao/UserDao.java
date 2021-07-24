package hu.bearmaster.tutorial.jpa.dao;

import hu.bearmaster.tutorial.jpa.model.User;

//Data Access Object
public class UserDao extends AbstractDao<User, Long> {
    
    public UserDao() {
        super("blogs-pu");
    }
    
    public User getUserById(Long id) {
        return getById(id);
    }
    
    public User create(User user) {
        return runInTransaction(entityManager -> {
            entityManager.persist(user);
            return user;
        });
    }
    
    public User update(User user) {
        return runInTransaction(entityManager -> entityManager.merge(user));
    }
    
    public void remove(User user) {
        runInTransaction(entityManager -> {
            User reference = entityManager.getReference(User.class, user.getId());
            entityManager.remove(reference);
            return null;
        });
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

}
