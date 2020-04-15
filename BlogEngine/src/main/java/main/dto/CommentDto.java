package main.dto;

import main.model.PostComment;

import java.time.format.DateTimeFormatter;

public class CommentDto {
    public int id;
    public String time;
    public UserByCommentDto user;
    public String text;

    public CommentDto(PostComment comment) {
        this.id = comment.getId();
        this.time = comment.getTime().format(DateTimeFormatter.ofPattern("y-MM-dd в H:mm"));
        this.user = new UserByCommentDto(comment.getUser());
        this.text = comment.getText();
    }
}
