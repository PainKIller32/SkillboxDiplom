package main.dto;

import java.util.List;

public class PostsDto<T> {
    public int count;
    public List<T> posts;

    public PostsDto(int count, List<T> posts) {
        this.count = count;
        this.posts = posts;
    }
}