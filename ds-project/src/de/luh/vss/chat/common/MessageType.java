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

    public static AbstractMessage fromInt(int typeId, DataInputStream in) throws IOException {
        for (MessageType type : MessageType.values()) {
            if (type.getTypeId() == typeId) {
                try {
                    // Erzeugt die Nachricht basierend auf dem Nachrichtentyp und dem InputStream
                    return type.messageClass.getConstructor(DataInputStream.class).newInstance(in);
                } catch (Exception e) {
                    throw new IOException("Fehler beim Instanziieren der Nachricht.", e);
                }
            }
        }
        throw new IllegalArgumentException("Unbekannter Nachrichtentyp ID: " + typeId);
    }

    public static MessageType fromString(String str) {
        for (MessageType type : MessageType.values()) {
            if (type.name().equalsIgnoreCase(str)) {
                return type;
            }
        }
        return null;
    }
}
