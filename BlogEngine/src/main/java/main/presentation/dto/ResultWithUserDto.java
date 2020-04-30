package main.presentation.dto;

public class ResultWithUserDto extends ResultDto {
    public final UserByAuthDto user;
    public ResultWithUserDto(boolean result, UserByAuthDto user) {
        super(result);
        this.user = user;
    }
}