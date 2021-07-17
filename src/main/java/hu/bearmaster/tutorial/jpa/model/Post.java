package hu.bearmaster.tutorial.jpa.model;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "posts", schema = "blogs")
@SequenceGenerator(name = "postIdGenerator", sequenceName = "posts_seq", schema = "blogs", initialValue = 1, allocationSize = 1)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postIdGenerator")
    private Long id;

    private String title;

    private String description;

    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    private int likes;

    private String slug;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    public Post() {}
    
    public Post(String title, String description, ZonedDateTime createdOn, int likes, String slug) {
        this.title = title;
        this.description = description;
        this.createdOn = createdOn;
        this.likes = likes;
        this.slug = slug;
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

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
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
        if (this.author != null) {
            removeAuthor();
        }
        if (author != null) {
            this.author = author;
            author.getPosts().add(this);
        }
    }
    
    private void removeAuthor() {
        this.author.getPosts().remove(this);
        this.author = null;
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", title=" + title + ", authorId=" + Optional.ofNullable(author).map(User::getId).orElse(null) + "]";
    }

    public static Post post(String title, String description) {
        String slug = "/" + Normalizer.normalize(title, Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks},\\p{Punct}]+", "")
                .toLowerCase()
                .replace(' ', '-');
        return new Post(title, description, ZonedDateTime.now(), 0, slug);
    }

}
