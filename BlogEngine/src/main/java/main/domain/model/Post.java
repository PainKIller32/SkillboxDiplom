package main.domain.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "is_active", columnDefinition = "TINYINT(1) NOT NULL")
    private boolean active;
    @Column(name = "moderation_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus;
    @Column(name = "moderator_id", columnDefinition = "INT")
    private int moderatorId;
    @Column(name = "user_id", columnDefinition = "INT NOT NULL")
    private int userId;
    @Column(columnDefinition = "DATETIME NOT NULL")
    private LocalDateTime time;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;
    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;
    @Column(name = "view_count", columnDefinition = "INT NOT NULL")
    private int viewCount;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "postId", cascade = CascadeType.ALL)
    private List<PostVote> votes;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "postId", cascade = CascadeType.ALL)
    private List<PostComment> comments;
    @ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tag2post", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
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

    public List<PostVote> getVotes() {
        return votes;
    }

    public void setVotes(List<PostVote> votes) {
        this.votes = votes;
    }

    public List<PostComment> getComments() {
        return comments;
    }

    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLikeCount() {
        int likeCount = 0;
        for (PostVote postVote : votes) {
            if (postVote.getValue() > 0) {
                likeCount++;
            }
        }
        return likeCount;
    }

    public int getDislikeCount() {
        int dislikeCount = 0;
        for (PostVote postVote : votes) {
            if (postVote.getValue() < 0) {
                dislikeCount++;
            }
        }
        return dislikeCount;
    }

    public List<String> getTags() {
        List<String> tags = new ArrayList<>();
        for (Tag tag : this.tags) {
            tags.add(tag.getName());
        }
        return tags;
    }

    public int getCommentCount() {
        return comments.size();
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}