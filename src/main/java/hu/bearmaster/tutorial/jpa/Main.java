package hu.bearmaster.tutorial.jpa;

import hu.bearmaster.tutorial.jpa.dao.PostDao;
import hu.bearmaster.tutorial.jpa.dao.UserDao;
import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.User;

public class Main {
    
    private final UserDao userDao = new UserDao();
    private final PostDao postDao = new PostDao();

    public static void main(String[] args) {
        Main main = new Main();
        main.associatePostToUser();
    }
    
    private void createPost() {
        Post post = Post.post("Ez egy teszt", "Ide jön a leírás");
        
        Post savedPost = postDao.create(post);
        
        System.out.println(savedPost);
    }

    private void findPost() {
        Post post = postDao.getPostById(8L);
        
        System.out.println(post);
    }
    
    private void findUser() {
        User user = userDao.getUserById(1L);
        
        System.out.println(user);
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
}
