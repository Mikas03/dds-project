package de.luh.vss.chat.client;

import de.luh.vss.chat.common.AbstractMessage;
import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.RegisterRequest;
import de.luh.vss.chat.common.UserId;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class InputHandler implements Runnable {
    private final ChatClient client;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final UserId userId;
    private final Scanner scanner;

    // Konstruktor, der die ChatClient-Instanz sowie Input/Output Streams und UserId übergibt
    public InputHandler(ChatClient client, DataInputStream in, DataOutputStream out, UserId userId) {
        this.client = client;
        this.in = in;
        this.out = out;
        this.userId = userId;
        this.scanner = new Scanner(System.in);
    }

    // Nachricht senden
    public void sendMessage(AbstractMessage msg) throws IOException {
        synchronized (out) {
            out.writeInt(msg.getClass().getSimpleName().hashCode());  // Identifiziert den Nachrichtentyp
            msg.toStream(out);  // Seriellen Stream in den DataOutputStream schreiben
        }
    }

    @Override
    public void run() {
        handleInput();
    }

    // Eingabe des Benutzers behandeln
    private void handleInput() {
        try {
            while (true) {
                System.out.print("Geben Sie eine Nachricht ein: ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("exit")) {
                    break;  // Wenn der Benutzer "exit" eingibt, wird der Chat beendet
                }

                // Beispiel einer Chat-Nachricht: Annehmen von "EmpfängerID" und "Priorität"
                System.out.print("Empfänger-ID: ");
                String recipientId = scanner.nextLine();

                System.out.print("Priorität: ");
                int priority = Integer.parseInt(scanner.nextLine());

                // Hier wieder eine Instanziierung von UserId
                ChatMessage chatMessage = new ChatMessage(userId, new UserId(Integer.parseInt(recipientId)), message, priority);
                sendMessage(chatMessage);  // Nachricht an den Server senden
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Benutzerregistrierung an den Server senden
    public void registerUser(UserId userId) {
        RegisterRequest registerRequest = new RegisterRequest(userId);
        try {
            sendMessage(registerRequest);  // Nachricht an den Server senden
            System.out.println("Benutzer registriert: " + userId.getId());
      } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
