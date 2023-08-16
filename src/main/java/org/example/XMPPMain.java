package org.example;

import java.util.*;

public class XMPPMain {
    public static void main(String[] args) {
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
                        List<String> contacts = new ArrayList<>();
                        do {
                            System.out.println("\nMAIN MENU\n-------------------------------------------");
                            System.out.println("1.  Show contacts");
                            System.out.println("2.  Add contact");
                            System.out.println("3.  Accept friend requests");
                            System.out.println("4.  Direct messages");
                            System.out.println("5.  Group messages");
                            System.out.println("6.  Send notification");
                            System.out.println("7.  Set presence message");
                            System.out.println("9.  Delete account");
                            System.out.println("10. Log out");
                            System.out.print("Select an option: ");
                            lgchoice = scanner.nextInt();
                            scanner.nextLine();
                            switch (lgchoice) {
                                case 1:
                                    contacts = xmppClient.getContacts();
                                    if (contacts.isEmpty()) {
                                        System.out.println("You don't have any contacts in your list.");
                                    } else {
                                        System.out.println("\n=================================");
                                        System.out.println("List of contacts:");
                                        for (String contact : contacts) {
                                            System.out.println(contact);
                                        }
                                        System.out.println("=================================");
                                    }
                                    break;
                                case 2:
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
                                case 3:
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
                                case 4:
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
                                            scanner.nextLine(); // Consumir la nueva línea pendiente
                                            if (sendMessageChoice == 1) {
                                                System.out.print("Mensage: ");
                                                String chatMessage = scanner.nextLine();
                                                xmppClient.sendMessage(selectedContact, chatMessage);
                                                System.out.println("Message sent ✓");
                                            }
                                        } else {
                                            System.out.println("Invalid option.");
                                        }
                                    }
                                    break;
                                case 5:
                                    boolean running = true;

                                    while (running) {
                                        System.out.println("\n1. Chats");
                                        System.out.println("2. Send Messages");
                                        System.out.println("3. Exit");
                                        System.out.print("Select an option: ");
                                        int choiceContacts = scanner.nextInt();

                                        switch (choiceContacts) {
                                            case 1:
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
                                                    System.out.print("Select the contact's number to whom you want to see the history: ");
                                                    int selectedContactIndex = scanner.nextInt();
                                                    scanner.nextLine();
                                                    if (selectedContactIndex >= 1 && selectedContactIndex <= contacts.size()) {
                                                        String selectedContact = contacts.get(selectedContactIndex - 1);
                                                        List<String> chatHistory = xmppClient.getChatHistory(selectedContact);

                                                        System.out.println("\nChat history with " + selectedContact + ":");
                                                        for (String me : chatHistory) {
                                                            System.out.println(me);
                                                        }
                                                    } else {
                                                        System.out.println("Invalid option.");
                                                    }
                                                }
                                                break;
                                            case 2:
                                                scanner.nextLine(); // Consume la nueva línea pendiente
                                                System.out.print("Ingresa el JID del contacto: ");
                                                String recipientJID = scanner.nextLine();
                                                System.out.print("Mensaje: ");
                                                String chatMessage = scanner.nextLine();
                                                xmppClient.sendMessage(recipientJID, chatMessage);
                                                System.out.println("Mensaje enviado.");
                                                break;
                                            case 3:
                                                running = false;
                                                break;
                                            default:
                                                System.out.println("Opción inválida.");
                                                break;
                                        }
                                    }
                                case 10:
                                    messageHistory = xmppClient.getMessageHistory();
                                    xmppClient.disconnect();
                                    break;
                                default:
                                    System.out.println("Invalid option.");
                            }
                        } while (lgchoice != 10);
                    };
                    break;
                case 2:
                    xmppClient.login("estrada20565", "admin");
                    scanner.nextLine(); // Consume the newline character
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
