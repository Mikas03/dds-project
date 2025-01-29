package de.luh.vss.chat.client;

import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.AbstractMessage;
import de.luh.vss.chat.common.MessageType;
import de.luh.vss.chat.common.User.UserId;

import java.io.*;
import java.net.*;
import java.util.PriorityQueue;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private UserId userId;
    private InputHandler inputHandler;

    // Verwende eine PriorityQueue, um die Nachrichten nach Priorität zu sortieren
    private final PriorityQueue<ChatMessage> messageQueue = new PriorityQueue<>();

    public static void main(String[] args) {
        new ChatClient().start();
    }

    public void start() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            
            inputHandler = new InputHandler(this, in, out);
            
            // Benutzerregistrierung
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Geben Sie Ihren Benutzernamen ein: ");
            String username = reader.readLine();
            userId = new UserId(username);
            
            inputHandler.registerUser(userId);
            
            // Eingabeverarbeitung starten
            new Thread(() -> inputHandler.handleInput()).start();

            // Nachrichten empfangen
            new Thread(this::receiveMessages).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Nachricht senden mit Priorität
    public void sendChatMessage(String recipient, String message, int priority) {
        ChatMessage chatMessage = new ChatMessage(userId, new UserId(recipient), message, priority);

        // Füge die Nachricht der Prioritäts-Warteschlange hinzu
        messageQueue.add(chatMessage);

        try {
            sendMessage(chatMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(ChatMessage msg) throws IOException {
        synchronized (out) {
            out.writeInt(msg.getClass().getSimpleName().hashCode());
            msg.toStream(out);  // Nachricht über den Outputstream senden
        }
    }

    // Nachrichten empfangen
    private void receiveMessages() {
        try {
            while (true) {
                int type = in.readInt();
                AbstractMessage msg = MessageType.fromInt(type, in);

                if (msg instanceof ChatMessage) {
                    ChatMessage chatMessage = (ChatMessage) msg;
                    System.out.println("Nachricht von " + chatMessage.getSender().id() + ": " + chatMessage.getMessage() + " (Priorität: " + chatMessage.getPriority() + ")");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Zeige die Warteschlange der gesendeten Nachrichten nach Priorität
 // Zeige die Warteschlange der gesendeten Nachrichten nach Priorität
    public void showMessageQueue() {
        System.out.println("Aktuelle Nachrichtenschlange:");
        if (messageQueue.isEmpty()) {
            System.out.println("Die Warteschlange ist leer.");
        } else {
            PriorityQueue<ChatMessage> sortedQueue = new PriorityQueue<>(messageQueue);  // Sortiere die Warteschlange
            while (!sortedQueue.isEmpty()) {
                ChatMessage message = sortedQueue.poll();
                System.out.println("Nachricht an " + message.getRecipient().id() + 
                                   " mit Priorität " + message.getPriority() + ": " + 
                                   message.getMessage());
            }
        }
    }

}
