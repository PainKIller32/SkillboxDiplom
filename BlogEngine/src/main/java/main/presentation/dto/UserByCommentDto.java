package main.presentation.dto;

import main.domain.model.User;

public class UserByCommentDto extends UserDto {
    public final String photo;

    public UserByCommentDto(User user) {
        super(user);
        this.photo = user.getPhoto() == null ? "" : user.getPhoto();
    }
}
