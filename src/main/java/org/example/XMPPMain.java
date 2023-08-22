/**
 * Nombre del Archivo: XMPPMain.java
 * Descripción: Este archivo contiene la clase principal que actúa como punto de entrada para la aplicación de chat XMPP.
 * Autor: Oscar Estrada
 * Fecha: 22/08/2023
 * Versión: 1.0
 *
 * Notas:
 * - Esta aplicación utiliza la biblioteca Smack para la comunicación XMPP.
 * - Asegúrese de que las credenciales de inicio de sesión y la configuración del servidor sean correctas.
 */

package org.example;

import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.stringprep.XmppStringprepException;
import java.util.*;

/**
 * This class represents the main entry point of the XMPP chat application.
 */
public class XMPPMain {

    /**
     * The main method that starts the XMPP chat application.
     *
     * @param args The command-line arguments (not used in this application).
     * @throws XmppStringprepException If there's an error with XMPP string preparation.
     */
    public static void main(String[] args) throws XmppStringprepException {
        Scanner scanner = new Scanner(System.in);
        int choice;
        int lgchoice;
        Map<String, List<String>> messageHistory = new HashMap<>();


        do {
            XMPPClient xmppClient = new XMPPClient(messageHistory);
            System.out.println("MENU");
            System.out.println("1. Login");
            System.out.println("2. Register new user");
            System.out.println("3. Close app");
            System.out.print("Select an option: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    scanner.nextLine();
                    System.out.print("\nUsername: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    if (xmppClient.login(username, password)){
                        System.out.println("\nLogged in successfully!");
                        xmppClient.sendConnectionNotificationToFriends();
                        List<String> contacts;
                        do {
                            System.out.println("\nMAIN MENU\n-------------------------------------------");
                            System.out.println("1.  Show contacts");
                            System.out.println("2.  View user information");
                            System.out.println("3.  Add contact");
                            System.out.println("4.  Accept friend requests");
                            System.out.println("5.  Switch Presence Mode");
                            System.out.println("6.  Direct messages");
                            System.out.println("7.  Send Files");
                            System.out.println("8.  Group messages");
                            System.out.println("9.  Delete account");
                            System.out.println("10. Log out");
                            System.out.print("Select an option: ");
                            lgchoice = scanner.nextInt();
                            scanner.nextLine();
                            switch (lgchoice) {
                                case 1:
                                    List<String> mainContacts = xmppClient.getContactsWithStatus();
                                    if (mainContacts.isEmpty()) {
                                        System.out.println("You don't have any contacts in your list.");
                                    } else {
                                        System.out.println("\n=================================");
                                        System.out.println("List of contacts:");
                                        for (String contact : mainContacts) {
                                            System.out.println(contact);
                                        }
                                        System.out.println("=================================");
                                    }
                                    break;
                                case 2:
                                    System.out.print("Enter the userJID (contact@domain): ");
                                    String targetUser = scanner.nextLine();
                                    String userStatus = xmppClient.getUserStatus(targetUser);
                                    System.out.println("\n==============================================");
                                    System.out.println(userStatus);
                                    break;
                                case 3:
                                    System.out.print("\nContact email (example@alumchat.xyz): ");
                                    String addEmail = scanner.nextLine();
                                    System.out.print("Nickname: ");
                                    String nickname = scanner.nextLine();
                                    boolean added = xmppClient.addContact(addEmail, nickname);
                                    if (added) {
                                        System.out.println("Contact successfully added..");
                                    } else {
                                        System.out.println("Contact could not be added.");
                                    }
                                    break;
                                case 4:
                                    List<String> subscriptionRequests = xmppClient.getSubscriptionRequests();
                                    if (subscriptionRequests.isEmpty()) {
                                        System.out.println("\nYou don't have any requests.");
                                    } else {
                                        System.out.println("\nList of requests:");
                                        for (String request : subscriptionRequests) {
                                            System.out.println("Incoming subscription request from: " + request);
                                        }
                                        System.out.println("\nPress 1 to accept all requests or 0 to exit: ");
                                        int acorden = scanner.nextInt();

                                        if (acorden == 1) {
                                            xmppClient.acceptAllRequests();
                                            System.out.println("Accepted all subscription requests.");
                                        } else if (acorden == 0) {
                                            System.out.println("Exiting...");
                                        } else {
                                            System.out.println("Invalid choice.");
                                        }
                                    }

                                    break;
                                case 5:
                                    System.out.println("\nSelect presence mode:");
                                    System.out.println("_________________________________");
                                    System.out.println("1. Available");
                                    System.out.println("2. Chat");
                                    System.out.println("3. Away");
                                    System.out.println("4. Extended Away");
                                    System.out.println("5. Do Not Disturb");
                                    System.out.println("0. Cancel");
                                    System.out.print("Enter your choice: ");
                                    int presenceModeChoice = scanner.nextInt();
                                    scanner.nextLine();

                                    Presence.Mode selectedPresenceMode = null;
                                    String statusMessage = null;

                                    switch (presenceModeChoice) {
                                        case 1:
                                            selectedPresenceMode = Presence.Mode.available;
                                            break;
                                        case 2:
                                            selectedPresenceMode = Presence.Mode.chat;
                                            break;
                                        case 3:
                                            selectedPresenceMode = Presence.Mode.away;
                                            break;
                                        case 4:
                                            selectedPresenceMode = Presence.Mode.xa;
                                            break;
                                        case 5:
                                            selectedPresenceMode = Presence.Mode.dnd;
                                            break;
                                        case 0:
                                            System.out.println("Operation cancelled.");
                                            break;
                                        default:
                                            System.out.println("Invalid choice.");
                                    }

                                    if (selectedPresenceMode != null) {
                                        System.out.print("Enter status message (optional): ");
                                        statusMessage = scanner.nextLine();

                                        if (xmppClient.setPresenceMode(selectedPresenceMode, statusMessage)) {
                                            System.out.println("Presence mode updated successfully.");
                                        } else {
                                            System.out.println("Failed to update presence mode.");
                                        }
                                    }
                                    break;
                                case 6:
                                    contacts = xmppClient.getContacts();

                                    if (contacts.isEmpty()) {
                                        System.out.println("You don't have any contacts in your list.");
                                    } else {
                                        System.out.println("\n===================================================");
                                        System.out.println("List of contacts:");
                                        for (int i = 0; i < contacts.size(); i++) {
                                            System.out.println((i + 1) + ". " + contacts.get(i));
                                        }
                                        System.out.println("===================================================\n");
                                        System.out.print("Select the contact's number to whom you want to see the chat: ");
                                        int selectedContactIndex = scanner.nextInt();
                                        scanner.nextLine();
                                        if (selectedContactIndex >= 1 && selectedContactIndex <= contacts.size()) {
                                            String selectedContact = contacts.get(selectedContactIndex - 1);
                                            List<String> chatHistory = xmppClient.getChatHistory(selectedContact);

                                            System.out.println("\nChat with " + selectedContact + ":");
                                            for (String me : chatHistory) {
                                                System.out.println(me);
                                            }

                                            System.out.print("\nDo you want to send a message to " + selectedContact + "? (1 for Yes, 0 for No): ");
                                            int sendMessageChoice = scanner.nextInt();
                                            scanner.nextLine();
                                            if (sendMessageChoice == 1) {
                                                System.out.print("Message: ");
                                                String chatMessage = scanner.nextLine();
                                                xmppClient.sendMessage(selectedContact, chatMessage, null);
                                                System.out.println("Message sent ✓");
                                            }
                                        } else {
                                            System.out.println("Invalid option.");
                                        }
                                    }
                                    break;
                                case 7:
                                    contacts = xmppClient.getContacts();
                                    if (contacts.isEmpty()) {
                                        System.out.println("You don't have any contacts in your list.");
                                    } else {
                                        System.out.println("\n===================================================");
                                        System.out.println("List of contacts:");
                                        for (int i = 0; i < contacts.size(); i++) {
                                            System.out.println((i + 1) + ". " + contacts.get(i));
                                        }
                                        System.out.println("===================================================\n");
                                        System.out.print("Select the contact's number to whom you want to send a file: ");
                                        int selectedContactIndex = scanner.nextInt();
                                        scanner.nextLine();
                                        if (selectedContactIndex >= 1 && selectedContactIndex <= contacts.size()) {
                                            String selectedContact = contacts.get(selectedContactIndex - 1);

                                            System.out.print("File path: ");
                                            String filePath = scanner.nextLine();
                                            xmppClient.sendFile(selectedContact, filePath);
                                            System.out.println("File sent ✓");
                                        } else {
                                            System.out.println("Invalid option.");
                                        }
                                    }
                                    break;
                                case 8:
                                    System.out.println("\n1. Invitar a usuario a sala de chat grupal");
                                    System.out.println("2. Crear sala de chat grupal");
                                    System.out.println("3. Unirse a una sala de chat grupal");
                                    System.out.println("4. Enviar mensaje a sala de chat grupal");
                                    System.out.println("5. Salir");

                                    System.out.print("Seleccione una opción: ");
                                    int option = scanner.nextInt();
                                    scanner.nextLine();

                                    switch (option) {
                                        case 1:
                                            System.out.print("Nombre de la sala: ");
                                            String roomName = scanner.nextLine();
                                            System.out.print("JID del usuario a invitar: ");
                                            String userJID = scanner.nextLine();
                                            xmppClient.inviteUserToGroupChat(roomName, userJID);
                                            break;

                                        case 2:
                                            System.out.print("Nombre de la sala: ");
                                            String newRoomName = scanner.nextLine();
                                            xmppClient.createGroupChatAndInvite(newRoomName);
                                            xmppClient.registerGroupMessageListener(newRoomName);
                                            break;
                                        case 3:
                                            xmppClient.acceptInvitationAndJoinGroupChat("Prueba4");
                                            xmppClient.registerGroupMessageListener("Prueba4");
                                            break;
                                        case 4:
                                            System.out.print("Nombre de la sala a la que enviar el mensaje: ");
                                            String targetRoom = scanner.nextLine();
                                            System.out.print("Mensaje a enviar: ");
                                            String message = scanner.nextLine();
                                            xmppClient.sendMessageToGroupChat(targetRoom, message);
                                            break;
                                        case 5:
                                            xmppClient.disconnect();
                                            System.exit(0);
                                        default:
                                            System.out.println("Opción inválida. Intente de nuevo.");
                                    }
                                    break;
                                case 9:
                                    System.out.println("Are you sure you want to delete your account?");
                                    System.out.println("Press 1 to confirm or 0 to cancel:");
                                    int confirmDelete = scanner.nextInt();

                                    if (confirmDelete == 1) {
                                        boolean deleted = xmppClient.deleteAccount();
                                        if (deleted) {
                                            System.out.println("Account deleted successfully.");
                                            xmppClient.disconnect();
                                            lgchoice = 10;
                                        } else {
                                            System.out.println("Failed to delete account.");
                                        }
                                    } else {
                                        System.out.println("Account deletion cancelled.");
                                    }
                                    break;
                                case 10:
                                    messageHistory = xmppClient.getMessageHistory();
                                    xmppClient.disconnect();
                                    break;

                                default:
                                    System.out.println("Invalid option.");
                            }
                        } while (lgchoice != 9);
                    };
                    break;
                case 2:
                    xmppClient.login("estrada20565", "admin");
                    scanner.nextLine(); 
                    System.out.print("\nNew Username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("New Password: ");
                    String newPassword = scanner.nextLine();
                    boolean accountCreated = xmppClient.createAccount(newUsername, newPassword);
                    if (accountCreated) {
                        xmppClient.disconnect();
                        System.out.println("Account created!");
                    }
                    break;
                case 3:
                    System.out.println("Thanks for using the program.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } while (choice != 3);
    }
}
