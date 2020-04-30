package main.presentation.dto;

public class ResultDto {
    public final boolean result;

    public ResultDto() {
        result = true;
    }

    public ResultDto(boolean result) {
        this.result = result;
    }

    public static ResultDto success() {
        return new ResultDto(true);
    }

    public static ResultDto decline() {
        return new ResultDto(false);
    }
}