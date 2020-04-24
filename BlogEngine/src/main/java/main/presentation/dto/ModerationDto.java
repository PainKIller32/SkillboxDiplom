package main.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModerationDto {
    @JsonProperty("post_id")
    private int postId;
    private String decision;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}