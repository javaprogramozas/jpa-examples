package hu.bearmaster.tutorial.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import hu.bearmaster.tutorial.jpa.dao.UserDao;
import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.User;
import hu.bearmaster.tutorial.jpa.model.UserStatus;

public class CascadeMain {
    
    private final UserDao userDao = new UserDao();

    public static void main(String[] args) {
        CascadeMain main = new CascadeMain();
        main.refresh();
    }
    
    private void persist() {
        User user = User.user("persist-user");
        Post post = Post.post("Cascade type: Persist", "A függő objektum is mentésre kerül, amikor a szülőt mentik");
        
        user.getPosts().add(post);
        post.setAuthor(user);
        userDao.create(user);
        
        System.out.println("Author: " + post.getAuthor());
    }
    
    private void merge() {
        User user = userDao.getUserById(1L);
        Post post = user.getPosts().iterator().next();
        
        user.setStatus(UserStatus.PENDING);
        post.setLikes(100);
        System.out.println("Updating user: " + user);
        userDao.update(user);
        
        System.out.println(post);
    }
    
    private void remove() {
        User user = userDao.getUserById(41L);
        
        userDao.remove(user);
        
        System.out.println("Deleted user: " + user);
        
    }
    
    private void detach() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        User user = entityManager.find(User.class, 1L);
        Post post = user.getPosts().iterator().next();
        
        entityManager.detach(user);
        
        System.out.println("User managed? " + entityManager.contains(user));
        System.out.println("Post managed? " + entityManager.contains(post));
        
    }
    
    private void refresh() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        User user = entityManager.find(User.class, 1L);
        
        System.out.println(user);
        System.out.println(user.getPosts());
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();        
        entityManager.createQuery( "update User u set status = 'ACTIVE' where u.id = 1" ).executeUpdate();
        entityManager.createQuery( "update Post p set likes = 200 where p.author.id = 1" ).executeUpdate();
        transaction.commit();
        
        System.out.println("Before refresh");
        System.out.println(user);
        System.out.println(user.getPosts());
        
        entityManager.refresh(user);
        
        System.out.println("After refresh");
        System.out.println(user);
        System.out.println(user.getPosts());
    }

}
