package main.presentation.dto;

import main.domain.model.Post;

public class PostFullViewDto extends PostDto {
    public transient String announce;
    public transient int commentCount;

    public PostFullViewDto(Post post) {
        super(post);
    }
}