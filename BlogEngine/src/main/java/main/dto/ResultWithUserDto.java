package main.dto;

public class ResultWithUserDto extends ResultDto {
    public UserByAuthDto user;
    public ResultWithUserDto(boolean result, UserByAuthDto user) {
        super(result);
        this.user = user;
    }
}