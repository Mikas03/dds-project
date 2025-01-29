package de.luh.vss.chat.common.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.luh.vss.chat.common.AbstractMessage;
import de.luh.vss.chat.common.User.UserId;

public class ChatMessage extends AbstractMessage implements Comparable<ChatMessage> {
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

    // Überschreibe toStream() um die ChatMessage zu streamen.
    @Override
    public void toStream(DataOutputStream out) throws IOException {
        out.writeUTF(sender.id());
        out.writeUTF(recipient.id());
        out.writeUTF(message);
        out.writeInt(priority);
    }

    public static ChatMessage fromStream(DataInputStream in) throws IOException {
        UserId sender = new UserId(in.readUTF());
        UserId recipient = new UserId(in.readUTF());
        String message = in.readUTF();
        int priority = in.readInt();
        return new ChatMessage(sender, recipient, message, priority);
    }

    // Implementiere Comparable, um die Nachrichten nach Priorität zu vergleichen.
    @Override
    public int compareTo(ChatMessage o) {
        // Vergleicht die Prioritäten - Höhere Priorität kommt vorne.
        return Integer.compare(o.priority, this.priority);  // Umgekehrt, damit höhere Priorität "kleiner" ist
    }
}
