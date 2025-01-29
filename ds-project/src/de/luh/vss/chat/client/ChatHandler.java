package de.luh.vss.chat.client;

import java.util.PriorityQueue;

import de.luh.vss.chat.common.Message.ChatMessage;


public class ChatHandler {
    private final PriorityQueue<ChatMessage> messageQueue = new PriorityQueue<>();

    public void handleChatMessage(ChatMessage chatMessage) {
        synchronized (messageQueue) {
            messageQueue.add(chatMessage);
        }
        processMessages();
    }

    public void processMessages() {
        synchronized (messageQueue) {
            while (!messageQueue.isEmpty()) {
                ChatMessage msg = messageQueue.poll();
                System.out.println("Processing message: " + msg.getMessage());
            }
        }
    }
}

