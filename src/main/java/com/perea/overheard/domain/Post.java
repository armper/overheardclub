package com.perea.overheard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Post.
 */
@Entity
@Table(name = "post")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private Instant date;

    @OneToMany(mappedBy = "post")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("posts")
    private User user;

    @ManyToOne
    @JsonIgnoreProperties("posts")
    private Topic topic;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Post title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public Post content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getDate() {
        return date;
    }

    public Post date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Post comments(Set<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public Post addComment(Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
        return this;
    }

    public Post removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setPost(null);
        return this;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public Post user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Topic getTopic() {
        return topic;
    }

    public Post topic(Topic topic) {
        this.topic = topic;
        return this;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        return id != null && id.equals(((Post) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Post{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", content='" + getContent() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
