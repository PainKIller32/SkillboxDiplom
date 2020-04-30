package main.presentation.dto;

import main.domain.model.User;

public class UserByAuthDto extends UserByCommentDto {
    public final String email;
    public final boolean moderation;
    public final int moderationCount;
    public final boolean settings;

    public UserByAuthDto(User user, int moderationCount) {
        super(user);
        email = user.getEmail();
        moderation = user.isModerator();
        this.moderationCount = moderationCount;
        settings = moderation;
    }
}