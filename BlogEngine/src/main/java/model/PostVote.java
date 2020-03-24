package model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "user_id", columnDefinition = "INT NOT NULL")
    private int userId;
    @Column(name = "post_id", columnDefinition = "INT NOT NULL")
    private int postId;
    @Column(columnDefinition = "DATETIME NOT NULL")
    private LocalDate time;
    @Column(columnDefinition = "TINYINT NOT NULL")
    private int value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}