package main.dto;

public class UserRegisterDto {
    private String e_mail;
    private String password;
    private String captcha;
    private String captcha_secret;

    public String getEmail() {
        return e_mail;
    }

    public void setE_mail(String email) {
        this.e_mail = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaSecret() {
        return captcha_secret;
    }

    public void setCaptcha_secret(String captchaSecret) {
        this.captcha_secret = captchaSecret;
    }
}