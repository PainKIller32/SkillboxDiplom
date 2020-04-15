package main.dto;

public class CaptchaDto {
    public String secret;
    public String image;

    public CaptchaDto(String secret, String image) {
        this.secret = secret;
        this.image = image;
    }
}