package main.presentation.dto;

import java.util.List;

public class TagsDto {
    public final List<TagDto> tags;

    public TagsDto(List<TagDto> tags) {
        this.tags = tags;
    }
}