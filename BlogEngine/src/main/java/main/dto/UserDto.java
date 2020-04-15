package main.dto;

import main.model.User;

public class UserDto {
    public int id;
    public String name;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }
}