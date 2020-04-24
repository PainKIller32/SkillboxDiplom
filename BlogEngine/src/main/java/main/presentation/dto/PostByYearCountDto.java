package main.presentation.dto;

import main.domain.model.PostByYearCount;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class PostByYearCountDto {
    public LinkedList<Integer> years;
    public LinkedHashMap<String, Integer> posts;

    public PostByYearCountDto(List<PostByYearCount> posts, LinkedList<Integer> years) {
        this.years = years;
        this.posts = new LinkedHashMap<>();
        for (PostByYearCount post : posts) {
            String key = post.getTime().format(DateTimeFormatter.ISO_DATE);
            if (this.posts.containsKey(key)) {
                this.posts.put(key, post.getCount() + this.posts.get(key));
            } else {
                this.posts.put(post.getTime().format(DateTimeFormatter.ISO_DATE), post.getCount());
            }
        }
    }
}