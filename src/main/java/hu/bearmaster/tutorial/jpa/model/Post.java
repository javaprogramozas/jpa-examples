package hu.bearmaster.tutorial.jpa.model;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "posts", schema = "blogs")
@SequenceGenerator(name = "postIdGenerator", sequenceName = "posts_seq", schema = "blogs", initialValue = 1, allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "selectPostsByLikesAndTitle", 
            query = """
                    SELECT p
                    FROM Post p
                    WHERE p.likes > :likes
                    AND LOCATE(:word, p.title) > 0 
                    ORDER BY p.id DESC
                    """)
})
@NamedNativeQueries({
    @NamedNativeQuery(name = "usersWithPalindromName",
            resultClass = User.class,
            query = """
                    SELECT u.id, u.username, u.status, u.created_at
                    FROM blogs.users u
                    WHERE lower(u.username) = reverse(lower(u.username)) 
                    """)
})
@NamedEntityGraphs({
    @NamedEntityGraph(name = "postWithAuthor", attributeNodes = {
            @NamedAttributeNode("author")
    })
})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postIdGenerator")
    private Long id;

    private String title;

    @Basic(fetch = FetchType.LAZY)
    private String description;

    @Column(name = "created_on")
    private OffsetDateTime createdOn;

    private int likes;

    private String slug;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    private String topic;
    
    @OneToMany(mappedBy = "post")
    private Set<Comment> comments = new HashSet<>();
    
    @Version
    private int version;

    public Post() {}
    
    public Post(String title, String description, OffsetDateTime createdOn, int likes, String slug, String topic) {
        this.title = title;
        this.description = description;
        this.createdOn = createdOn;
        this.likes = likes;
        this.slug = slug;
        this.topic = topic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return String.format("%d. '%s' (%d likes)", id, title, likes);
    }

    public static Post post(String title, String description) {
        String slug = "/" + Normalizer.normalize(title, Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks},\\p{Punct}]+", "")
                .toLowerCase()
                .replace(' ', '-');
        return new Post(title, description, OffsetDateTime.now(), 0, slug, "teszt");
    }

}
