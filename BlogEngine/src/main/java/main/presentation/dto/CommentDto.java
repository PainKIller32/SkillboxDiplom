package main.presentation.dto;

import main.domain.model.PostComment;

import java.time.format.DateTimeFormatter;

public class CommentDto {
    public final int id;
    public final String time;
    public final UserByCommentDto user;
    public final String text;

    public CommentDto(PostComment comment) {
        this.id = comment.getId();
        this.time = comment.getTime().format(DateTimeFormatter.ofPattern("y-MM-dd в H:mm"));
        this.user = new UserByCommentDto(comment.getUser());
        this.text = comment.getText();
    }
}