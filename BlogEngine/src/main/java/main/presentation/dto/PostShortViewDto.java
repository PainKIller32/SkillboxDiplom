package main.presentation.dto;

import main.domain.model.Post;

import java.util.List;

public class PostShortViewDto extends PostDto {
    public transient String text;
    public transient List<CommentDto> comments;
    public transient List<String> tags;

    public PostShortViewDto(Post post) {
        super(post);
    }
}