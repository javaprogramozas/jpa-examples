package hu.bearmaster.tutorial.jpa.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "comments", schema = "blogs")
@SequenceGenerator(name = "commentIdGenerator", sequenceName = "comments_seq", schema = "blogs", initialValue = 1, allocationSize = 1)
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commentIdGenerator")
    private Long id;

    private String username;

    private String body;
    
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Comment [id=" + id + ", username=" + username + ", body=" + body + "]";
    }

}
