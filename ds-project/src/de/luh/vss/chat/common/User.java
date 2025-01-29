package de.luh.vss.chat.common;

public class User {
    public static class UserId {
        private final String id;

        public UserId(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    // Eine Benutzerklasse könnte später mit mehr Details wie Namen, E-Mail, etc. erweitert werden
    private final UserId userId;

    public User(UserId userId) {
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }
}
