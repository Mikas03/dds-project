package de.luh.vss.chat.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.RegisterRequest;
import de.luh.vss.chat.common.MessageType;

public abstract class AbstractMessage {
    
    // Methode zum Serialisieren der Nachricht (muss von Unterklassen implementiert werden)
    public abstract void toStream(DataOutputStream out) throws IOException;
    
    // Jede Nachricht muss ihren Typ zurückgeben (z. B. REGISTER_REQUEST, CHAT_MESSAGE)
    public abstract MessageType getMessageType();

    // Methode, um eine Nachricht aus einem Stream zu lesen
    public static AbstractMessage fromStream(DataInputStream in) throws IOException {
        int messageType = in.readInt();  // Nachrichtentyp als int einlesen
        MessageType type = MessageType.fromId(messageType); // Umwandlung in Enum

        switch (type) {
            case REGISTER_REQUEST:
                return RegisterRequest.fromStream(in);
            case CHAT_MESSAGE:
                return ChatMessage.fromStream(in);
            default:
                throw new IOException("Unbekannter Nachrichtentyp: " + messageType);
        }
    }
}
