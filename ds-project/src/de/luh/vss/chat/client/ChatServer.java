package de.luh.vss.chat.client;

import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.RegisterRequest;
import de.luh.vss.chat.common.User.UserId;
import de.luh.vss.chat.common.AbstractMessage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class ChatServer {
    private static final int SERVER_PORT = 12345;
    private ServerSocket serverSocket;
    private static final Map<UserId, ClientHandler> clients = new ConcurrentHashMap<>();
    private static final PriorityBlockingQueue<ChatMessage> messageQueue = new PriorityBlockingQueue<>(
            11,
            new Comparator<ChatMessage>() {
                @Override
                public int compare(ChatMessage m1, ChatMessage m2) {
                    // Höhere Priorität wird vorne sein, daher vergleichen wir umgekehrt
                    return Integer.compare(m2.getPriority(), m1.getPriority());
                }
            }
    );

    public static void main(String[] args) {
        new ChatServer().start();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server gestartet...");

            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        private UserId userId;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                // Benutzerregistrierung
                int messageTypeValue = in.readInt();  
                AbstractMessage msg = AbstractMessage.fromStream(in);
                if (msg instanceof RegisterRequest registerRequest) {
                    userId = registerRequest.getUserId();
                    clients.put(userId, this);
                    System.out.println("Benutzer " + userId + " verbunden.");
                }

                // Nachrichtenempfangsschleife
                while (true) {
                    messageTypeValue = in.readInt();  
                    msg = AbstractMessage.fromStream(in);

                    if (msg instanceof ChatMessage chatMessage) {
                        messageQueue.offer(chatMessage);  // Nachricht in die Warteschlange einfügen
                        processMessages();  // Nachrichten verarbeiten
                    }
                }
            } catch (IOException e) {
                System.out.println("Benutzer " + userId + " getrennt.");
                clients.remove(userId);
            }
        }

        // Prozessiert Nachrichten aus der Warteschlange
        private void processMessages() {
            synchronized (messageQueue) {
                System.out.println("Processing messages...");
                while (!messageQueue.isEmpty()) {
                    ChatMessage msg = messageQueue.poll();
                    System.out.println("Sending message to " + msg.getRecipient().id() + ": " + msg.getMessage() + " (Priority: " + msg.getPriority() + ")");
                    
                    UserId receiverId = msg.getRecipient();
                    ClientHandler receiver = clients.get(receiverId);

                    if (receiver != null) {
                        try {
                            receiver.sendMessage(msg);
                        } catch (IOException e) {
                            System.out.println("Failed to send message to " + receiverId);
                        }
                    }
                }
            }
            System.out.println("Warteschlange nach dem Verarbeiten: ");
            for (ChatMessage msg : messageQueue) {
                System.out.println(msg.getMessage() + " - Priorität: " + msg.getPriority());
            }
        }




        // Sendet eine Nachricht an den Client
        public void sendMessage(ChatMessage msg) throws IOException {
            synchronized (out) {
                msg.toStream(out); // Nachricht an den Client senden
            }
        }
    }


}
