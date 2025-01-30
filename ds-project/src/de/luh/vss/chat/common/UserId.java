package de.luh.vss.chat.common;

public class UserId {
    private final int id;

    public UserId(int id) {
        this.id = id;
    }

    public int getId() {  // ⬅️ Methode muss getId() heißen!
        return id;
    }

    @Override
    public String toString() {
        return "user" + id;
    }
}
