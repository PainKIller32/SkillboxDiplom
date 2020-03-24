package model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "parent_id", columnDefinition = "INT")
    private int parentId;
    @Column(name = "post_id", columnDefinition = "INT NOT NULL")
    private int postId;
    @Column(name = "user_id", columnDefinition = "INT NOT NULL")
    private int userId;
    @Column(columnDefinition = "DATETIME NOT NULL")
    private LocalDate time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }
}