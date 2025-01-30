package de.luh.vss.chat.client;

import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.RegisterRequest;
import de.luh.vss.chat.common.UserId;
import de.luh.vss.chat.common.AbstractMessage;
import de.luh.vss.chat.common.MessageType;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";  // Server-Adresse
    private static final int SERVER_PORT = 12345;              // Server-Port

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private UserId userId;
    private Scanner scanner;
    private int userPort;

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }

    public void start() {
        try {
            // Verbindung zum Server aufbauen
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            scanner = new Scanner(System.in);

            // Benutzername eingeben
            System.out.print("Geben Sie Ihre Benutzer-ID ein (Zahl): ");
            int userIdNumber = Integer.parseInt(scanner.nextLine());
            userId = new UserId(userIdNumber);  // Benutzer-ID erstellen
            userPort = userIdNumber + 3000;

            // Registrierungsanfrage senden
            RegisterRequest registerRequest = new RegisterRequest(userId);
            sendMessage(registerRequest);

            // Eingabe-Handler starten
            Thread inputHandlerThread = new Thread(this::handleInput);
            inputHandlerThread.start();

            // Nachrichten empfangen
            receiveMessages();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(AbstractMessage message) throws IOException {
        out.writeInt(message.getMessageType().getTypeId()); // Nachrichtentyp senden
        message.toStream(out);
    }

    private void handleInput() {
        try {
            while (true) {
                System.out.print("Geben Sie eine Nachricht ein: ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }

                System.out.print("Empfänger-ID: ");
                int recipientId = Integer.parseInt(scanner.nextLine());

                System.out.print("Priorität: ");
                int priority = Integer.parseInt(scanner.nextLine());

                ChatMessage chatMessage = new ChatMessage(userId, new UserId(recipientId), message, priority);
                sendMessage(chatMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        try (DatagramSocket dSocket = new DatagramSocket(userPort)) {
        	byte[] buffer = new byte[1024];
            while (true) {
            	
            	DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            	dSocket.receive(packet);
            	byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
            	DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
            	try {
            		ChatMessage message = ChatMessage.fromStream(dataIn);
            		System.out.println("you received message: " + message.getMessage());
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
