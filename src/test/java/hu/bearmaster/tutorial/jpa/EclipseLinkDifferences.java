package hu.bearmaster.tutorial.jpa;

import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.junit.jupiter.api.Test;

import hu.bearmaster.tutorial.jpa.model.Post;
import hu.bearmaster.tutorial.jpa.model.Post_;
import hu.bearmaster.tutorial.jpa.model.User;
import hu.bearmaster.tutorial.jpa.model.UserStatus;

class EclipseLinkDifferences {
    
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("blogs-pu");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    // ZonedDateTime not supported out of the box, see model classes
    
    // table identifier is mandatory: FROM Post p
    @Test
    void basicQuery() {
        List<Post> posts = entityManager.createQuery("FROM Post p", Post.class).getResultList();
        
        System.out.println(posts);
    }
    
    // Enum value to String not converted in JPQL queries, e.g. u.status = 'ACTIVE'
    @Test
    void traverseSingleRelationShipOneToMany() {
        // Posts with authors in PENDING status
        String query = """
                FROM Post p
                WHERE p.author.status = hu.bearmaster.tutorial.jpa.model.UserStatus.ACTIVE
                """;
        
        List<Post> posts = entityManager.createQuery(query, Post.class).getResultList();
        
        System.out.println(posts);
    }
    
    // Type casting is more strict OffsetDateTime -> String
    @Test
    void basicQueryWithProjectionInSelect() {
        // SELECT p.title FROM Post p
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OffsetDateTime> query = builder.createQuery(OffsetDateTime.class);
        Root<Post> post = query.from(Post.class);
        query.select(post.get(Post_.createdOn));
        
        List<OffsetDateTime> titles = entityManager.createQuery(query).getResultList();
        
        System.out.println(titles.get(0));
    }

    // FROM keyword is mandatory in DELETE JPQL statement 
    @Test
    void simpleDelete() {
        // Delete inactive users
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery("DELETE FROM User u WHERE u.status = hu.bearmaster.tutorial.jpa.model.UserStatus.INACTIVE")
                .executeUpdate();
        
        System.out.println("Removed " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    // Native queries only support positional parameters, e.g. :status -> ?1
    @Test
    void nativeQueryWithMapping() {        
        String query = """
                SELECT u.id, u.username, u.status, u.created_at
                FROM blogs.users u
                WHERE lower(u.username) = reverse(lower(u.username)) 
                AND u.status = ?1
                """;
        List<User> palindromUsers = entityManager.createNativeQuery(query, User.class)
                .setParameter(1, UserStatus.ACTIVE.name())
                .getResultList();
        
        System.out.println(palindromUsers);
    }
    
    // Subquery issues: https://bugs.eclipse.org/bugs/show_bug.cgi?id=444610
    @Test
    void updateRelation() {
        // Update posts without author
        // UPDATE Post p SET p.author = (SELECT u FROM User u WHERE u.id = :userId) WHERE p.author IS NULL
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Post> update = builder.createCriteriaUpdate(Post.class);
        
        Root<Post> post = update.from(Post.class);
        Path<User> author = post.get("author");
        
        Subquery<User> subquery = update.subquery(User.class);
        Root<User> user = subquery.from(User.class);
        subquery.select(user);
        subquery.where(builder.equal(user.get("id"), 1L));
        
        update.set(author, subquery);
        update.where(builder.isNull(author));
        
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        int affectedRows = entityManager.createQuery(update).executeUpdate();
        
        System.out.println("Updated " + affectedRows + " records");
        
        transaction.rollback();
    }
    
    // -javaagent:e:\tmp\repo\org\eclipse\persistence\org.eclipse.persistence.jpa\2.7.9\org.eclipse.persistence.jpa-2.7.9.jar
    @Test
    void honorsFetchTypeWhenWeavingActive() {
        List<User> users = entityManager.createQuery("FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", 1L)
                .getResultList();
        
        System.out.println(users);
    }
    
    @Test
    void noLazyInitException() {
        User user = entityManager.createQuery("FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", 1L)
                .getSingleResult();
        
        entityManager.close();
        
        System.out.println(user);
        System.out.println(user.getRoles());
        System.out.println(user.getPosts());
        
        for (Post post : user.getPosts()) {
            System.out.println(post);
        }
    }
    
    @Test
    void entityGraphWithLoadHint() {
        EntityGraph<?> graph = entityManager.getEntityGraph("userWithPosts");
        
        User user = entityManager.createQuery("FROM User u WHERE u.id = :id", User.class)
                .setHint("javax.persistence.loadgraph", graph)
                .setParameter("id", 1L)
                .getSingleResult();
        
        entityManager.close();
        
        System.out.println(user);
        System.out.println(user.getRoles());
        System.out.println(user.getPosts());
    }
    
    @Test
    void entityGraphWithFetchHint() {
        EntityGraph<?> graph = entityManager.getEntityGraph("userWithPosts");
        
        User user = entityManager.createQuery("FROM User u WHERE u.id = :id", User.class)
                .setHint("javax.persistence.fetchgraph", graph)
                .setParameter("id", 1L)
                .getSingleResult();
        
        entityManager.close();
        
        System.out.println(user.getPosts());
        System.out.println(user);
        System.out.println(user.getRoles());

    }
    
    @Test
    void entityGraphWithFetchHintMultipleResults() {
        EntityGraph<?> graph = entityManager.getEntityGraph("userWithPosts");
        
        List<User> users = entityManager.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.posts p", User.class)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();
        
        entityManager.close();
        
        for (User user : users) {
            System.out.println(user.getId() + " => " + user.getPosts());
        }
    }

}
