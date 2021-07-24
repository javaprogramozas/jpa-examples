package hu.bearmaster.tutorial.jpa.dao;

import hu.bearmaster.tutorial.jpa.model.Post;

public class PostDao extends AbstractDao<Post, Long> {
    
    public PostDao() {
        super("blogs-pu");
    }
    
    public Post getPostById(Long id) {
        return getById(id);
    }
    
    public Post create(Post post) {
        return runInTransaction(entityManager -> {
            entityManager.persist(post);
            return post;
        });
    }
    
    public Post update(Post post) {
        return runInTransaction(entityManager -> entityManager.merge(post));
    }
    
    public void remove(Post post) {
        runInTransaction(entityManager -> {
            Post reference = entityManager.getReference(Post.class, post.getId());
            entityManager.remove(reference);
            return null;
        });
    }

    @Override
    protected Class<Post> getEntityClass() {
        return Post.class;
    }

}
