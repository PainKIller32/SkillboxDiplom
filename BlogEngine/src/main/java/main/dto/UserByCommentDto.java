package main.dto;

import main.model.User;

public class UserByCommentDto extends UserDto {
    public String photo;

    public UserByCommentDto(User user) {
        super(user);
        this.photo = user.getPhoto() == null ? "" : user.getPhoto();
    }
}
