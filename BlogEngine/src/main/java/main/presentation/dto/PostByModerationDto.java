package main.presentation.dto;

import main.domain.model.Post;

import java.util.List;

public class PostByModerationDto extends PostDto {
    public transient String text;
    public transient int likeCount;
    public transient int dislikeCount;
    public transient int commentCount;
    public transient int viewCount;
    public transient List<CommentDto> comments;
    public transient List<String> tags;

    public PostByModerationDto(Post post) {
        super(post);
    }
}