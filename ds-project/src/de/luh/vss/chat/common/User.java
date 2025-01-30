package de.luh.vss.chat.common;

public class User {
    private final UserId userId;

    public User(UserId userId) {
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }
}
