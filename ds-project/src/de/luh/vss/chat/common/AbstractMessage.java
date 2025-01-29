package de.luh.vss.chat.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class AbstractMessage {

    public abstract void toStream(DataOutputStream out) throws IOException;

    public static AbstractMessage fromStream(DataInputStream in) throws IOException {
        // Diese Methode soll von den spezifischen Nachrichten implementiert werden
        // Der Rückgabewert wird in den spezifischen Nachrichtentypen sein, z.B. ChatMessage
        return null;
    }
}
