package hu.bearmaster.tutorial.jpa.locking;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

import hu.bearmaster.tutorial.jpa.model.Post;

public class OptimisticLocking {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        List<Post> posts = entityManager.createQuery("FROM Post p ORDER BY p.id", Post.class)
                .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
                .getResultList();
        
        posts.forEach(post -> System.out.println(post));
        
        System.out.print("Which do you like? ");
        Scanner input = new Scanner(System.in);
        Long id = Long.valueOf(input.nextLine());
        
        Optional<Post> candidatePost = posts.stream().filter(post -> post.getId().equals(id)).findAny();
        
        if (candidatePost.isPresent()) {
            Post postToUpdate = candidatePost.get();
            //entityManager.lock(postToUpdate, LockModeType.OPTIMISTIC);
            int increasedLikes = postToUpdate.getLikes() + 1;
            
            System.out.println("Increasing likes of post '" + postToUpdate.getTitle()
                    + "' from " + postToUpdate.getLikes() + " to " + increasedLikes);
            postToUpdate.setLikes(increasedLikes);
        } else {
            System.out.println("No post found with id " + id + ", exiting...");
        }
        
        transaction.commit();
        input.close();
        entityManager.close();
    }

}
