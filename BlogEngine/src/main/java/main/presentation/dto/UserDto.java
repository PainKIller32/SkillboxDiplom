package main.presentation.dto;

import main.domain.model.User;

public class UserDto {
    public final int id;
    public final String name;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }
}