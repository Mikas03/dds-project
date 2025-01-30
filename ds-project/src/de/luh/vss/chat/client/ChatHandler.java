package de.luh.vss.chat.client;

import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.RegisterRequest;
import de.luh.vss.chat.common.UserId;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ChatHandler implements Runnable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ChatHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                int messageType = in.readInt(); // Empfange den Nachrichtentyp
                if (messageType == 1) {  // RegisterRequest
                    RegisterRequest registerRequest = RegisterRequest.fromStream(in);
                    handleRegister(registerRequest);
                } else if (messageType == 2) {  // ChatMessage
                    ChatMessage chatMessage = ChatMessage.fromStream(in);
                    handleChatMessage(chatMessage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void handleRegister(RegisterRequest registerRequest) {
        UserId userId = registerRequest.getUserId();
        System.out.println("Benutzer registriert: " + userId.getId());
    }


    private void handleChatMessage(ChatMessage chatMessage) throws IOException {
        System.out.println("Nachricht von " + chatMessage.getSender().getId() + " an " +
                chatMessage.getRecipient().getId() + ": " + chatMessage.getMessage());
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        int port = 3000 + chatMessage.getRecipient().getId();
        byte[] buffer;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        		DataOutputStream dOut = new DataOutputStream(outputStream)) {
        	chatMessage.toStream(dOut);
        	buffer = outputStream.toByteArray();
        }
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, port);
        try (DatagramSocket dSocket = new DatagramSocket(3000)) {
        	dSocket.send(packet);
        }
        
    }


    private void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
