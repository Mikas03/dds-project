package de.luh.vss.chat.client;

import de.luh.vss.chat.common.AbstractMessage;
import de.luh.vss.chat.common.Message.RegisterRequest;
import de.luh.vss.chat.common.User.UserId;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class InputHandler {
    private final ChatClient client;
    private final DataInputStream in;
    private final DataOutputStream out;

    // Konstruktor, der die ChatClient-Instanz sowie Input/Output Streams übergibt
    public InputHandler(ChatClient client, DataInputStream in, DataOutputStream out) {
        this.client = client;
        this.in = in;
        this.out = out;
    }

    // Nachricht senden
    public void sendMessage(AbstractMessage msg) throws IOException {
        synchronized (out) {
            out.writeInt(msg.getClass().getSimpleName().hashCode());  // Identifiziert den Nachrichtentyp
            msg.toStream(out);  // Seriellen Stream in den DataOutputStream schreiben
        }
    }

    // Eingabe des Benutzers behandeln
    public void handleInput() {
        Scanner scanner = new Scanner(System.in);

        // Haupt-Loop für Benutzereingaben
        while (true) {
            System.out.println("\nGeben Sie eine Option ein (exit, send, show):");

            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                client.close();
                System.out.println("Verbindung geschlossen.");
                break;
            }

            // Nachricht an einen Empfänger senden
            if (command.startsWith("send")) {
                String[] parts = command.split(" ", 3);
                if (parts.length == 3) {
                    String recipient = parts[1];
                    String message = parts[2];
                    System.out.print("Geben Sie die Priorität der Nachricht ein: ");
                    int priority = Integer.parseInt(scanner.nextLine());
                    client.sendChatMessage(recipient, message, priority); // Nachricht senden
                } else {
                    System.out.println("Bitte verwenden Sie das Format: send <Empfänger> <Nachricht>");
                }
            }

            // Nachrichtenschlange anzeigen
            if (command.equalsIgnoreCase("show")) {
                client.showMessageQueue();  // Alle Nachrichten anzeigen
            }
        }
    }

    // Benutzerregistrierung an den Server senden
    public void registerUser(UserId userId) {
        RegisterRequest registerRequest = new RegisterRequest(userId);
        try {
            sendMessage(registerRequest);  // Nachricht an den Server senden
            System.out.println("Benutzer registriert: " + userId.id());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
