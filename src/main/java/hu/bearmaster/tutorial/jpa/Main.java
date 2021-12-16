package hu.bearmaster.tutorial.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import hu.bearmaster.tutorial.jpa.dao.PostDao;
import hu.bearmaster.tutorial.jpa.dao.UserDao;
import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.User;
import hu.bearmaster.tutorial.jpa.model.UserStatus;

public class Main {
    
    private final UserDao userDao = new UserDao();
    private final PostDao postDao = new PostDao();

    public static void main(String[] args) {
        Main main = new Main();
        main.findPost();
    }
    
    private void createPost() {
        Post post = Post.post("Ez egy teszt", "Ide jön a leírás");
        
        Post savedPost = postDao.create(post);
        
        System.out.println(savedPost);
    }

    private void findPost() {
        Post post = postDao.getPostById(8L);
        
        System.out.println(post);
        /*
        Post postWithAuthor = postDao.runInTransaction(entityManager -> {
            User user = entityManager.find(User.class, post.getAuthor().getId());
            post.setAuthor(user);
            return post;
        });
        */
        System.out.println(post.getAuthor().getId());
        User author = userDao.getUserById(post.getAuthor().getId());
        System.out.println(author);
    }
    
    private void findUser() {
        User user = userDao.getUserById(1L);
        
        System.out.println(user);
        System.out.println(user.getPosts());
    }
    
    
    private void associatePostToUser() {
        User user = userDao.getUserById(1L);
        Post post = postDao.getPostById(12L);
        System.out.println(user);
        
        post.setAuthor(user);
        postDao.update(post);
        System.out.println(user);
    }
    
    
    private void disassociatePostFromUser() {
        User user = userDao.getUserById(1L);
        System.out.println(user);
        
        user.getPosts().remove(0);
        userDao.update(user);
        System.out.println(user);
    }
    
    /*
    private void deletePost() {
        Post post = postDao.getPostById(24L);
        
        System.out.println(post);
        
        postDao.remove(post);
    }
    */
    
    private void persistentContext() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        // New or transient
        User user = User.user("testuser");
        
        // Managed or persistent
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        System.out.println(user);
        transaction.commit();
        
        User foundUser = entityManager.find(User.class, 1L);
        
        // Detached
        entityManager.close();
        System.out.println(foundUser);
        
        // Make entity attached again
        entityManager = entityManagerFactory.createEntityManager();
        
        System.out.println("Managed? " + entityManager.contains(foundUser));
        User mergedUser = entityManager.merge(foundUser);
        System.out.println(mergedUser);
        System.out.println(mergedUser.getPosts());
        System.out.println("Managed? " + entityManager.contains(mergedUser));
        entityManager.refresh(mergedUser);
        
        // Removed
        transaction = entityManager.getTransaction();
        transaction.begin();
        User userToRemove = entityManager.find(User.class, user.getId());
        entityManager.remove(userToRemove);
        transaction.commit();
        
        // Changing entities
        System.out.println("-------------\n");
        User anotherUser = entityManager.find(User.class, 2L);
        System.out.println(anotherUser);
        transaction = entityManager.getTransaction();
        transaction.begin();
        anotherUser.setStatus(UserStatus.PENDING);
        transaction.commit();
    }
}
