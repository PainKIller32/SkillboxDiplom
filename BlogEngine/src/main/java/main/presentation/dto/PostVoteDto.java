package main.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostVoteDto {
    @JsonProperty("post_id")
    private int postId;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
