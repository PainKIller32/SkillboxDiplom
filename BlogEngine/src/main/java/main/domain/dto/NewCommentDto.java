package main.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewCommentDto {
    @JsonProperty("parent_id")
    private Integer parentId;
    @JsonProperty("post_id")
    private int postId;
    private String text;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}