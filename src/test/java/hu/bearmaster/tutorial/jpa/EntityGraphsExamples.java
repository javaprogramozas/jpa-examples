package hu.bearmaster.tutorial.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Subgraph;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.Post_;
import hu.bearmaster.tutorial.jpa.model.User;
import hu.bearmaster.tutorial.jpa.model.UserStatus;
import hu.bearmaster.tutorial.jpa.model.User_;

class EntityGraphsExamples {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    @Test
    void defaultGraph() {
        User user = entityManager.createQuery("FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", 1L)
                .getSingleResult();
        
        entityManager.close();
        safePrint("User: %s", user);
        safePrint("Roles: %s", user.getRoles());
        safePrint("Posts: %s", user.getPosts());
    }
    
    @Test
    void entityGraphWithLoadHint() {
        User user = loadUser(1L, "javax.persistence.loadgraph", "userWithPosts");
        safePrint("User: %s", user);
        safePrint("Roles: %s", user.getRoles());
        safePrint("Posts: %s", user.getPosts());
    }
    
    @Test
    void entityGraphWithFetchHint() {        
        User user = loadUser(1L, "javax.persistence.fetchgraph", "userWithPosts");
        safePrint("User: %s", user);
        safePrint("Roles: %s", user.getRoles());
        safePrint("Posts: %s", user.getPosts());
    }
    
    @Test
    void entityGraphWithMultipleAttributes() {        
        User user = loadUser(1L, "javax.persistence.fetchgraph", "userWithPostsAndRoles");
        safePrint("User: %s", user);
        safePrint("Roles: %s", user.getRoles());
        safePrint("Posts: %s", user.getPosts());
    }
    
    @Test
    void hintsAreNotSetInStone() {
        // Enable addresses
        User user = loadUser(1L, "javax.persistence.fetchgraph", "userWithPosts");
        safePrint("User: %s", user);
        safePrint("Roles: %s", user.getRoles());
        safePrint("Posts: %s", user.getPosts());
    }
    
    @Test
    void subGraphs() {
        User user = loadUser(1L, "javax.persistence.fetchgraph", "userWithPostsAndComments");
        safePrint("User: %s", user);
        safePrint("Roles: %s", user.getRoles());
        safePrint("Posts: %s", user.getPosts());
    }
    
    @Test
    void dynamicEntityGraph() {
        // Miért nem lettem inkább pék?
        // https://stackoverflow.com/questions/45409305/jpa-entitygraph-create-subgraph-of-pluralattribute-programmably-by-using-static
        
        EntityGraph<User> graph = entityManager.createEntityGraph(User.class);
        //graph.addAttributeNodes(User_.roles);
        //graph.addAttributeNodes(User_.posts);
        Subgraph<Post> subgraph = graph.addSubgraph("posts", Post.class);
        subgraph.addAttributeNodes(Post_.comments);
        
        User user = entityManager.createQuery("FROM User u WHERE u.id = :id", User.class)
                .setHint("javax.persistence.fetchgraph", graph)
                .setParameter("id", 1L)
                .getSingleResult();
        
        entityManager.close();
        safePrint("User: %s", user);
        safePrint("Roles: %s", user.getRoles());
        safePrint("Posts: %s", user.getPosts());
    }
    
    @Test
    void fetchJoinWithDynamicEntityGraph() {
        // Posts with 5+ likes and active authors
        // SELECT p FROM Post p JOIN FETCH p.author u WHERE p.likes > 5 AND u.status = 'ACTIVE'
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);
        Join<Post, User> author = post.join(Post_.author);
        EntityGraph<Post> fetchGraph = entityManager.createEntityGraph(Post.class);
        fetchGraph.addSubgraph(Post_.author, User.class);
        
        query.select(post);
        query.where(builder.gt(post.get(Post_.likes), 5), builder.equal(author.get(User_.status), UserStatus.ACTIVE));
        
        List<Post> posts = entityManager.createQuery(query)
                .setHint("javax.persistence.fetchgraph", fetchGraph)
                .getResultList();
        
        for (Post p : posts) {
            System.out.println(p.getTitle() + " from " + p.getAuthor().getUsername());
        }
    }
    
    // merge???
    
    private User loadUser(Long id, String hint, String graphName) {
        EntityGraph<?> graph = entityManager.getEntityGraph(graphName);
        
        User user = entityManager.createQuery("FROM User u WHERE u.id = :id", User.class)
                .setHint(hint, graph)
                .setParameter("id", id)
                .getSingleResult();
        
        entityManager.close();
        return user;
    }
    
    private static void safePrint(String format, Object... objects) {
        try {
            System.out.println(String.format(format, objects));
        } catch (Exception e) {
            System.out.println(String.format(format, "Failed with " + e.getClass().getSimpleName()));
        }
    }

}
