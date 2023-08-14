package org.example;

import java.util.List;
import java.util.Scanner;

public class XMPPMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        int lgchoice;
        XMPPClient xmppClient = new XMPPClient();
        do {
            System.out.println("MENU");
            System.out.println("1. Login");
            System.out.println("2. Register new user");
            System.out.println("3. Close app");
            System.out.print("Select an option: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    scanner.nextLine(); // Consume the newline character
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();


                    if (xmppClient.login(username, password)){
                        do {
                            System.out.println("\nMENU PRINCIPAL");
                            System.out.println("1. Mostrar contactos");
                            System.out.println("2. Agregar contacto");
                            System.out.println("3. Mostrar detalles de un usuario");
                            System.out.println("4. Mensajes directos");
                            System.out.println("5. Mensajes grupales");
                            System.out.println("6. Enviar notificacion");
                            System.out.println("7. Definir mensaje de presencia");
                            System.out.println("9. Borrar cuenta");
                            System.out.println("10. Cerrar sesion");
                            lgchoice = scanner.nextInt();
                            switch (lgchoice) {
                                case 1:
                                    List<String> contacts = xmppClient.getContacts();
                                    System.out.println("Lista de contactos:");
                                    for (String contact : contacts) {
                                        System.out.println(contact);
                                    }
                                    break;
                                case 2:
                                    System.out.print("Ingrese el correo de su contacto (contactJID@alumchat.xyz): ");
                                    String addCorreo = scanner.nextLine();
                                    System.out.print("Ingrese el nickname para su contacto: ");
                                    String nickname = scanner.nextLine();
                                    boolean added = xmppClient.addContact(addCorreo, nickname);
                                    if (added) {
                                        System.out.println("Contacto agregado exitosamente.");
                                    } else {
                                        System.out.println("No se pudo agregar el contacto.");
                                    }
                                    break;
                                case 3:
                                    boolean accepted = xmppClient.acceptContactRequest("estrada20565test1@alumchat.xyz");
                                    if (accepted) {
                                        System.out.println("Solicitud de contacto aceptada exitosamente.");
                                    } else {
                                        System.out.println("No se pudo aceptar la solicitud de contacto.");
                                    }
                                case 10:
                                    xmppClient.disconnect();
                                    System.out.println("Funciono");
                                    break;
                                default:
                                    System.out.println("Invalid option.");
                            }
                        } while (lgchoice != 10);
                    };
                    break;
                case 2:
                    System.out.println("Seleccionaste la opción 2.");
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
