package de.luh.vss.chat.common.Message;

import de.luh.vss.chat.common.UserId;
import de.luh.vss.chat.common.AbstractMessage;
import de.luh.vss.chat.common.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatMessage extends AbstractMessage {
    private final UserId sender;
    private final UserId recipient;
    private final String message;
    private final int priority;

    public ChatMessage(UserId sender, UserId recipient, String message, int priority) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.priority = priority;
    }

    public UserId getSender() {
        return sender;
    }

    public UserId getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException {
        out.writeInt(sender.getId());
        out.writeInt(recipient.getId());
        out.writeUTF(message);
        out.writeInt(priority);
    }

    public static ChatMessage fromStream(DataInputStream in) throws IOException {
        UserId sender = new UserId(in.readInt());
        UserId recipient = new UserId(in.readInt());
        String message = in.readUTF();
        int priority = in.readInt();
        return new ChatMessage(sender, recipient, message, priority);
    }
    
    @Override
    public MessageType getMessageType() {
        return MessageType.CHAT_MESSAGE;
    }

}
