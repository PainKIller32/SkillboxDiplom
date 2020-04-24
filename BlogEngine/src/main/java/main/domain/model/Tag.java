package main.domain.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;
    @ManyToMany(mappedBy = "tags")
    private List<Post> posts;

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public Double getFrequencyOfOccurrence() {
        double frequency = 0;
        for (Post post : posts) {
            if (post.getActive() == 1 && post.getModerationStatus().equals(ModerationStatus.ACCEPTED) && post.getTime().isBefore(LocalDateTime.now())) {
                frequency++;
            }
        }
        return frequency;
    }
}