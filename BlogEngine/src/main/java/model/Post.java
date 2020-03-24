package model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "is_active", columnDefinition = "TINYINT NOT NULL")
    private boolean isActive;
    @Column(name = "moderation_status", columnDefinition = "ENUM(‘NEW’, ‘ACCEPTED’, ‘DECLINED’) NOT NULL")
    private ModerationStatus moderationStatus;
    @Column(name = "moderator_id", columnDefinition = "INT")
    private int moderatorId;
    @Column(name = "user_id", columnDefinition = "INT NOT NULL")
    private int userId;
    @Column(columnDefinition = "DATETIME NOT NULL")
    private LocalDate time;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;
    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;
    @Column(name = "view_count", columnDefinition = "INT NOT NULL")
    private int viewCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public int getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}