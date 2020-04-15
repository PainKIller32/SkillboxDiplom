package main.dto;

import main.model.User;

public class UserByAuthDto extends UserByCommentDto {
    public String email;
    public boolean moderation;
    public int moderationCount;
    public boolean settings;

    public UserByAuthDto(User user, int moderationCount) {
        super(user);
        email = user.getEmail();
        moderation = user.isModerator();
        this.moderationCount = moderationCount;
        settings = moderation;
    }
}