package org.example;

import java.util.List;
import java.util.Scanner;

public class XMPPMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        int lgchoice;
        do {
            XMPPClient xmppClient = new XMPPClient();
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
                        do {
                            System.out.println("\nMAIN MENU\n-------------------------------------------");
                            System.out.println("1.  Show contacts");
                            System.out.println("2.  Add contact");
                            System.out.println("3.  Show user details");
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
                                    List<String> contacts = xmppClient.getContacts();
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
                                    List<String> listContacts = xmppClient.getContacts();

                                    if (listContacts.isEmpty()) {
                                        System.out.println("You don't have any contacts in your list.");
                                    } else {
                                        System.out.println("\n===================================================");
                                        System.out.println("List of contacts:");
                                        for (int i = 0; i < listContacts.size(); i++) {
                                            System.out.println((i + 1) + ". " + listContacts.get(i));
                                        }
                                        System.out.println("===================================================\n");
                                        System.out.print("Select the contact's number to whom you want to send a message: ");
                                        int selectedContactIndex = scanner.nextInt();
                                        scanner.nextLine();
                                        System.out.print("Write the message you want to send: ");
                                        String message = scanner.nextLine();
                                        if (selectedContactIndex >= 1 && selectedContactIndex <= listContacts.size()) {
                                            String selectedContactJID = listContacts.get(selectedContactIndex - 1);
                                            boolean messageSent = xmppClient.sendMessage(selectedContactJID, message);
                                            if (messageSent) {
                                                System.out.println("Message sent successfully.");
                                            } else {
                                                System.out.println("Error while sending the message.");
                                            }
                                        } else {
                                            System.out.println("Invalid option.");
                                        }
                                    }
                                    break;
                                case 5:
                                    boolean running = true;

                                    while (running) {
                                        System.out.println("\n1. List of contacts");
                                        System.out.println("2. Chats");
                                        System.out.println("3. Send Messages");
                                        System.out.println("4. Exit");
                                        System.out.print("Select an option: ");
                                        int choiceContacts = scanner.nextInt();

                                        switch (choiceContacts) {
                                            case 1:
                                                List<String> testContacts = xmppClient.getContacts();
                                                System.out.println("\nContacts:");
                                                for (String contact : testContacts) {
                                                    System.out.println(contact);
                                                }
                                                break;
                                            case 2:
                                                break;
                                            case 3:
                                                scanner.nextLine(); // Consume la nueva línea pendiente
                                                System.out.print("Ingresa el JID del contacto: ");
                                                String recipientJID = scanner.nextLine();
                                                System.out.print("Mensaje: ");
                                                String chatMessage = scanner.nextLine();
                                                xmppClient.sendMessage(recipientJID, chatMessage);
                                                System.out.println("Mensaje enviado.");
                                                break;
                                            case 4:
                                                running = false;
                                                break;
                                            default:
                                                System.out.println("Opción inválida.");
                                                break;
                                        }
                                    }
                                case 10:
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
