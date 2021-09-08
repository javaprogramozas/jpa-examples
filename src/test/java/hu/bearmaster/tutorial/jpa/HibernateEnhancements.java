package hu.bearmaster.tutorial.jpa;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.User;

class HibernateEnhancements {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    @Test
    void singularToPluralAttribute() {
        User user = User.user("enhanceUser");
        Post post = Post.post("Enhance this!", "Description");
        
        System.out.println("User posts: " + user.getPosts());
        System.out.println("Author: " + post.getAuthor());
        System.out.println();
        
        post.setAuthor(user);
        System.out.println("User posts: " + user.getPosts());
        System.out.println("Author: " + post.getAuthor());
    }
    
    @Test
    void pluralToSingularAttribute() {
        User user = User.user("enhanceUser");
        Post post = Post.post("Enhance this!", "Description");
        Post post2 = Post.post("Another one", "Description");
        
        System.out.println("User posts: " + user.getPosts());
        System.out.println("Author: " + post.getAuthor());
        System.out.println();
        
        user.setPosts(Set.of(post));
        System.out.println("User posts: " + user.getPosts());
        System.out.println("Author: " + post.getAuthor());
        System.out.println();
        
        user.getPosts().add(post2);
        System.out.println("User posts: " + user.getPosts());
        System.out.println("Author: " + post2.getAuthor());
        
    }
    
    @Test
    void singlePostWithoutDescription() {
        Post post = entityManager.createQuery("SELECT p FROM Post p WHERE p.id = 8", Post.class).getSingleResult();
        
        System.out.println("Title: " + post.getTitle());
        System.out.println("Description: " + post.getDescription());
    }
    
    @Test
    void update() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Post post = entityManager.createQuery("SELECT p FROM Post p WHERE p.id = 8", Post.class).getSingleResult();
        
        post.setLikes(post.getLikes() + 1);
        
        transaction.commit();
    }

}
