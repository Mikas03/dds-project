package de.luh.vss.chat.common.Message;

import de.luh.vss.chat.common.AbstractMessage;
import de.luh.vss.chat.common.MessageType;
import de.luh.vss.chat.common.UserId;

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
        out.writeInt(userId.getId());
    }

    public static RegisterRequest fromStream(DataInputStream in) throws IOException {
        int id = in.readInt();
        return new RegisterRequest(new UserId(id));
    }
    @Override
    public MessageType getMessageType() {
        return MessageType.REGISTER_REQUEST;
    }

}
