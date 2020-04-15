package main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

public class EditProfileDto {
    private String name;
    private String email;
    private String password;
    //@JsonProperty
    //private MultipartFile photo;
    private Integer removePhoto;

//    public MultipartFile getPhoto() {
//        return photo;
//    }
//
//    public void setPhoto(MultipartFile photo) {
//        this.photo = photo;
//    }

    public Integer getRemovePhoto() {
        return removePhoto;
    }

    public void setRemovePhoto(Integer removePhoto) {
        this.removePhoto = removePhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}