package main.presentation.dto;

public class ResultWithIdDto extends ResultDto {
    public transient boolean result;
    public final int id;

    public ResultWithIdDto(int id) {
        this.id = id;
    }
}