package main.presentation.dto;

import java.util.List;

public class PostsDto<T> {
    public final int count;
    public final List<T> posts;

    public PostsDto(int count, List<T> posts) {
        this.count = count;
        this.posts = posts;
    }
}