package de.luh.vss.chat.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server läuft und wartet auf Verbindungen...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Neue Verbindung von: " + socket.getInetAddress());

                // Starte einen neuen Thread für den Client
                ChatHandler chatHandler = new ChatHandler(socket);
                new Thread(chatHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
