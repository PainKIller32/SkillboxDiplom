package main.domain.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "is_moderator", columnDefinition = "TINYINT NOT NULL")
    private boolean isModerator;
    @Column(name = "reg_time", columnDefinition = "DATETIME NOT NULL")
    private LocalDateTime regTime;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String email;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String password;
    @Column(columnDefinition = "VARCHAR(255)")
    private String code;
    @Column(columnDefinition = "VARCHAR(255)")
    private String photo;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isModerator() {
        return isModerator;
    }

    public void setModerator(boolean moderator) {
        isModerator = moderator;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}