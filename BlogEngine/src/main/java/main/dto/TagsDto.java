package main.dto;

import java.util.List;

public class TagsDto {
    public List<TagDto> tags;

    public TagsDto(List<TagDto> tags) {
        this.tags = tags;
    }
}