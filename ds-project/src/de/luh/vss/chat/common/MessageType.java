package de.luh.vss.chat.common;

import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.RegisterRequest;

import java.io.DataInputStream;
import java.io.IOException;

public enum MessageType {
    REGISTER_REQUEST(1, RegisterRequest.class),
    CHAT_MESSAGE(2, ChatMessage.class);

    private final int typeId;
    private final Class<? extends AbstractMessage> messageClass;

    MessageType(int typeId, Class<? extends AbstractMessage> messageClass) {
        this.typeId = typeId;
        this.messageClass = messageClass;
    }

    public int getTypeId() {
        return typeId;
    }

    public static MessageType fromId(int typeId) {
        for (MessageType type : values()) {
            if (type.getTypeId() == typeId) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MessageType ID: " + typeId);
    }

	public AbstractMessage createMessageFromStream(DataInputStream in) throws IOException {
        try {
            return messageClass.getDeclaredConstructor(DataInputStream.class).newInstance(in);
        } catch (Exception e) {
            throw new IOException("Error creating message from stream", e);
        }
    }
}
