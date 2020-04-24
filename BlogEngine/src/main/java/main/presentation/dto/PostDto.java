package main.presentation.dto;

import main.domain.model.Post;
import main.domain.model.PostComment;
import org.jsoup.Jsoup;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class PostDto {
    public int id;
    public String time;
    public UserDto user;
    public String title;
    public String text;
    public String announce;
    public int likeCount;
    public int dislikeCount;
    public int commentCount;
    public int viewCount;
    public List<CommentDto> comments;
    public List<String> tags;

    public PostDto(Post post) {
        this.id = post.getId();
        this.time = post.getTime().format(DateTimeFormatter.ofPattern("y-MM-dd H:mm"));
        this.user = new UserDto(post.getUser());
        this.title = post.getTitle();
        this.text = post.getText();
        this.announce = Jsoup.parse(post.getText()).text();
        this.likeCount = post.getLikeCount();
        this.dislikeCount = post.getDislikeCount();
        this.commentCount = post.getCommentCount();
        this.viewCount = post.getViewCount();
        comments = new ArrayList<>();
        for (PostComment postComment : post.getComments()) {
            comments.add(new CommentDto(postComment));
        }
        tags = new ArrayList<>();
        this.tags = post.getTags();
    }
}