package de.luh.vss.chat.common.Message;

import de.luh.vss.chat.common.AbstractMessage;
import de.luh.vss.chat.common.User.UserId;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegisterRequest extends AbstractMessage {
    private final UserId userId;

    public RegisterRequest(UserId userId) {
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException {
        out.writeUTF(userId.id());
    }

    public static RegisterRequest fromStream(DataInputStream in) throws IOException {
        UserId userId = new UserId(in.readUTF());
        return new RegisterRequest(userId);
    }
}
