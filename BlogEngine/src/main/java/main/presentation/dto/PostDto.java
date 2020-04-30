package main.presentation.dto;

import main.domain.model.Post;
import main.domain.model.PostComment;
import org.jsoup.Jsoup;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class PostDto {
    public final int id;
    public final String time;
    public final UserDto user;
    public final String title;
    public final String text;
    public final String announce;
    public final int likeCount;
    public final int dislikeCount;
    public final int commentCount;
    public final int viewCount;
    public final List<CommentDto> comments;
    public final List<String> tags;

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
        this.tags = post.getTags();
    }
}