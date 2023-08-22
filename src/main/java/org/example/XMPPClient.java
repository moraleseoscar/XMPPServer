/**
 * Nombre del Archivo: XMPPClient.java
 * Descripción: Este archivo contiene la implementación de la clase XMPPClient, que maneja la funcionalidad de un cliente XMPP.
 * Autor: Oscar Estrada
 * Fecha: 22/08/2023
 * Versión: 1.0
 *
 * Dependencias Externas:
 * - Smack API: Biblioteca para la comunicación XMPP. Versión 4.2.0.
 * - JXMPP: Biblioteca para la manipulación de JID (Jabber ID).
 *
 * Notas:
 * - Esta clase implementa varias funcionalidades de un cliente XMPP, como la conexión al servidor, gestión de contactos,
 *   envío de mensajes, administración de estados de presencia y funciones de chat grupal.
 * - Asegúrate de completar la versión de las dependencias externas con las versiones reales utilizadas en tu proyecto.
 */

package org.example;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jivesoftware.smack.roster.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Esta clase implementa un cliente XMPP con diversas funcionalidades, como conexión, gestión de contactos,
 * envío de mensajes, estados de presencia y funciones de chat grupal.
 */
public class XMPPClient {
    private static final String XMPP_SERVER = "alumchat.xyz";
    private static final int PORT = 5222;
    private static final String DOMAIN = "alumchat.xyz";
    private String username;
    private String password;
    private AbstractXMPPConnection connection;
    private Roster roster;
    private List<String> incomingSubscriptionRequests = new ArrayList<>();
    private Map<String, List<String>> messageHistory;
    private List<String> notifications = new ArrayList<>();

    public XMPPClient(Map<String, List<String>> messageHistory) {
        connect();
        this.messageHistory = messageHistory;
    }

//=================================================================================================GENERAL CONECTION=================================================================================================

    /** 
     * @return boolean
     */
    public boolean connect() {
        try {
            // Configuración de la conexión
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(username, password)
                    .setXmppDomain(DOMAIN)
                    .setHost(XMPP_SERVER)
                    .setPort(PORT)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            // Creamos la conexión
            connection = new XMPPTCPConnection(config);
            connection.connect();

            // Manejo de las suscripciones entrantes
            Roster roster = Roster.getInstanceFor(connection);
            roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
            roster.addSubscribeListener((from, subscribeRequest) -> {
                incomingSubscriptionRequests.add(from.toString()); // Agrega la solicitud entrante a la lista
                return null;
            });

            registerMessageListener(stanza -> {
                if (stanza instanceof Message) {
                    Message message = (Message) stanza;
                    String notification = message.getBody();
                    if (notification != null) {
                        if(notification.contains(":")){
                            String[] parts = notification.split(": ");
                            if (parts[0].contains("File ")){
                                String[] meta = parts[0].split(" ");
                                String notKey = getChatKey(username, meta[2]);
                                String notificationFile = receiveFile(meta[2], meta[1], parts[1]);
                                addMessageToChatHistory(notKey, notificationFile);
                            }else{
                                String notKey = getChatKey(username, parts[0]); // Obtener la clave del chat
                                addMessageToChatHistory(notKey, notification);
                            }
                        }else{
                            String[] parts = notification.split(" ");
                            String notKey = getChatKey(username, parts[0]); // Obtener la clave del chat
                            addMessageToChatHistory(notKey, notification);
                        }

                    }
                }
            });

            System.out.println("\nSuccessful connection to the XMPP server.\n");
            return true;
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            System.err.println("Failed to establish connection to the XMPP server: " + e.getMessage());
            return false;
        } catch (SmackException | IOException | XMPPException | InterruptedException ex) {
            ex.printStackTrace();
            System.err.println("Failed to establish connection to the XMPP server: " + ex.getMessage());
            return false;
        }
    }

//=================================================================================================ACCOUNT SETS=================================================================================================

    /** 
     * @param username
     * @param password
     * @return boolean
     */
    public boolean login(String username, String password) {
        try {
            connection.login(username, password);
            this.username = username;
            this.password = password;
            return true;
        } catch (SASLErrorException saslError) {
            System.err.println("Login failed: Invalid credentials.");
            return false;
        } catch (SmackException | IOException | XMPPException | InterruptedException ex) {
            ex.printStackTrace();
            System.err.println("Failed to establish connection to the XMPP server: " + ex.getMessage());
            return false;
        }
    }

    public void disconnect() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
    }

    
    /** 
     * @param newUsername
     * @param newPassword
     * @return boolean
     */
    public boolean createAccount(String newUsername, String newPassword) {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            AccountManager accountManager = AccountManager.getInstance(connection);
            try {
                accountManager.sensitiveOperationOverInsecureConnection(true);
                accountManager.createAccount(Localpart.from(newUsername), newPassword);
                return true;
            } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException |
                     SmackException.NotConnectedException | InterruptedException ex) {
                ex.printStackTrace();
                System.err.println("Error encountered while creating the account: " + ex.getMessage());
            } catch (XmppStringprepException e) {
                throw new RuntimeException(e);
            }
        }else{
            System.out.print(connection != null);
            System.out.print(connection.isConnected());
            System.out.print(connection.isAuthenticated());
        }
        return false;
    }

    
    /** 
     * @return boolean
     */
    public boolean deleteAccount() {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            try {
                AccountManager accountManager = AccountManager.getInstance(connection);
                accountManager.deleteAccount();
                return true;
            } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException |
                     SmackException.NotConnectedException | InterruptedException ex) {
                ex.printStackTrace();
                System.err.println("Error encountered while deleting the account: " + ex.getMessage());
            }
        }
        return false;
    }

//=================================================================================================CONTACTS=================================================================================================

    /** 
     * @return List<String>
     */
    public List<String> getContacts() {
        List<String> contactList = new ArrayList<>();

        if (connection != null && connection.isConnected()) {
            roster = Roster.getInstanceFor(connection);
            roster.setSubscriptionMode(Roster.SubscriptionMode.manual);

            for (RosterEntry entry : roster.getEntries()) {
                contactList.add(entry.getJid().toString());
            }
        }

        return contactList;
    }

    
    /** 
     * @return List<String>
     */
    public List<String> getContactsWithStatus() {
        List<String> contactList = new ArrayList<>();

        if (connection != null && connection.isConnected()) {
            roster = Roster.getInstanceFor(connection);
            roster.setSubscriptionMode(Roster.SubscriptionMode.manual);

            for (RosterEntry entry : roster.getEntries()) {
                String contactJID = entry.getJid().toString();
                Presence presence = roster.getPresence(entry.getJid());

                String presenceStatus = "Unknown";
                String customStatusMessage = "";

                if (presence.isAvailable()) {
                    Presence.Mode presenceMode = presence.getMode();

                    if (presenceMode == Presence.Mode.available) {
                        presenceStatus = "Available";
                        customStatusMessage = presence.getStatus();
                        if (customStatusMessage == null) {
                            customStatusMessage = "...";
                        }
                    } else if (presenceMode == Presence.Mode.chat) {
                        presenceStatus = "Available (Chat)";
                    } else if (presenceMode == Presence.Mode.away) {
                        presenceStatus = "Away";
                    } else if (presenceMode == Presence.Mode.xa) {
                        presenceStatus = "Extended Away";
                    } else if (presenceMode == Presence.Mode.dnd) {
                        presenceStatus = "Do Not Disturb";
                    }
                } else {
                    presenceStatus = "Offline";
                }

                String contactInfo = contactJID + " (" + presenceStatus + ")";
                if (!customStatusMessage.isEmpty()) {
                    contactInfo += " - " + customStatusMessage;
                }
                contactList.add(contactInfo);
            }
        }

        return contactList;
    }

    
    /** 
     * @param targetUser
     * @return String
     * @throws XmppStringprepException
     */
    public String getUserStatus(String targetUser) throws XmppStringprepException {
        if (connection != null && connection.isConnected()) {
            roster = Roster.getInstanceFor(connection);
            roster.setSubscriptionMode(Roster.SubscriptionMode.manual);

            RosterEntry entry = roster.getEntry(JidCreate.bareFrom(targetUser));
            if (entry != null) {
                Presence presence = roster.getPresence(entry.getJid());

                String presenceStatus = "Unknown";
                String customStatusMessage = "";

                if (presence.isAvailable()) {
                    Presence.Mode presenceMode = presence.getMode();

                    if (presenceMode == Presence.Mode.available) {
                        presenceStatus = "Available";
                        customStatusMessage = presence.getStatus();
                        if (customStatusMessage == null) {
                            customStatusMessage = "...";
                        }
                    } else if (presenceMode == Presence.Mode.chat) {
                        presenceStatus = "Available (Chat)";
                    } else if (presenceMode == Presence.Mode.away) {
                        presenceStatus = "Away";
                    } else if (presenceMode == Presence.Mode.xa) {
                        presenceStatus = "Extended Away";
                    } else if (presenceMode == Presence.Mode.dnd) {
                        presenceStatus = "Do Not Disturb";
                    }
                } else {
                    presenceStatus = "Offline";
                }

                return "User: " + targetUser + "\nStatus: " + presenceStatus + "\nMessageStatus: " + customStatusMessage + "\n==============================================";
            } else {
                return "User not found in your roster.\n==============================================";
            }
        }

        return "Not connected to the XMPP server.";
    }

//=================================================================================================PRESENCE=================================================================================================
    
    /** 
     * @param presenceMode
     * @param statusMessage
     * @return boolean
     */
    public boolean setPresenceMode(Presence.Mode presenceMode, String statusMessage) {
        if (connection != null && connection.isConnected()) {
            Presence presence = new Presence(Presence.Type.available);

            presence.setMode(presenceMode);
            if (statusMessage != null && !statusMessage.isEmpty()) {
                presence.setStatus(statusMessage);
            }

            List<String> friends = getContacts();
            String newStatusNotification = "";
            if (presenceMode == Presence.Mode.available) {
                newStatusNotification = "'Available'";
            } else if (presenceMode == Presence.Mode.chat) {
                newStatusNotification = "'Available (Chat)'";
            } else if (presenceMode == Presence.Mode.away) {
                newStatusNotification = "'Away'";
            } else if (presenceMode == Presence.Mode.xa) {
                newStatusNotification = "'Extended Away'";
            } else if (presenceMode == Presence.Mode.dnd) {
                newStatusNotification = "'Do Not Disturb'";
            }
            try {
                connection.sendStanza(presence);

                String notificationMessage = username + " has updated his presence to " + newStatusNotification;

                for (String friend : friends) {
                    try {
                        EntityBareJid jid = JidCreate.entityBareFrom(friend);
                        ChatManager chatManager = ChatManager.getInstanceFor(connection);
                        Chat chat = chatManager.chatWith(jid);
                        chat.send(notificationMessage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.err.println("Error encountered while sending message: " + ex.getMessage());
                    }
                }

                return true;
            } catch (SmackException.NotConnectedException | InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

//=================================================================================================MESSAGES=================================================================================================

    /** 
     * @param contactJID
     * @param messageBody
     * @param base64File
     * @return boolean
     */
    public boolean sendMessage(String contactJID, String messageBody, String base64File) {
        if (connection != null && connection.isConnected()) {
            try {
                EntityBareJid jid = JidCreate.entityBareFrom(contactJID);
                ChatManager chatManager = ChatManager.getInstanceFor(connection);
                Chat chat = chatManager.chatWith(jid);
                String contact = contactJID.replace("@alumchat.xyz", "");
                String key = getChatKey(username, contact);
                if (base64File != null && !base64File.isEmpty()) {
                    String[] data = base64File.split(": ");
                    String formattedMessageToSend = "File " + data[0] + " " + username + ": " + data[1];
                    String formattedMessageToSave = "You sent the file: " + data[0] + " to " + contact;
                    addMessageToChatHistory(key, formattedMessageToSave);
                    chat.send(formattedMessageToSend);
                } else {
                    String formattedMessage = username + ": " + messageBody;
                    addMessageToChatHistory(key, formattedMessage);
                    chat.send(formattedMessage);
                }

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Error encountered while sending message: " + ex.getMessage());
            }
        }
        return false;
    }
    
    /** 
     * @param user1
     * @param user2
     * @return String
     */
    private String getChatKey(String user1, String user2) {
        if (user1.compareTo(user2) < 0) {
            return user1 + " " + user2;
        } else {
            return user2 + " " + user1;
        }
    }
    
    /** 
     * @param key
     * @param message
     */
    public void addMessageToChatHistory(String key, String message) {
        List<String> chatHistory = messageHistory.getOrDefault(key, new ArrayList<>());
        chatHistory.add(message);
        messageHistory.put(key, chatHistory);
    }
    
    /** 
     * @param contactJID
     * @return List<String>
     */
    public List<String> getChatHistory(String contactJID) {
        String key = getChatKey(username, contactJID.replace("@alumchat.xyz", ""));
        return messageHistory.getOrDefault(key, new ArrayList<>());
    }

    /**
     * @return Map<String, List<String>>
     */
    public Map<String, List<String>> getMessageHistory(){return this.messageHistory;}

//=================================================================================================FILES================================================================================================
    
    /** 
     * @param contactJID
     * @param filePath
     * @return boolean
     */
    public boolean sendFile(String contactJID, String filePath) {
        try {
            File archivo = new File(filePath);
            String nombreArchivo = archivo.getName();
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            String base64File = Base64.getEncoder().encodeToString(fileBytes);
            String content = nombreArchivo + ": " + base64File;

            return sendMessage(contactJID, "", content);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error while sending file: " + ex.getMessage());
        }
        return false;
    }

    /** 
     * @param user
     * @param nameFile
     * @param base64File
     * @return String
     */
    public String receiveFile(String user, String nameFile, String base64File) {
        String savePath = "C:/Users/oestr/OneDrive/Escritorio/recibirPrueba/" + nameFile;
        try {
            byte[] fileBytes = Base64.getDecoder().decode(base64File);
            Files.write(Paths.get(savePath), fileBytes);
            return (user + " sent you a file and was saved at " + savePath);
        } catch (IOException ex) {
            ex.printStackTrace();
            return ("Error while receiving file: " + ex.getMessage());
        }
    }

//=================================================================================================CONTACTS=================================================================================================
    
    /** 
     * @param contactJID
     * @param nickname
     * @return boolean
     */
    public boolean addContact(String contactJID, String nickname) {
        if (connection != null && connection.isConnected()) {
            try {
                Roster roster = Roster.getInstanceFor(connection);
                roster.createEntry(JidCreate.bareFrom(contactJID), nickname, null);
                return true;
            } catch (SmackException | InterruptedException | IOException | XMPPException.XMPPErrorException ex) {
                ex.printStackTrace();
                System.err.println("Error while adding contact: " + ex.getMessage());
            }
        }
        return false;
    }

    
    /** 
     * @return boolean
     */
    public boolean acceptAllRequests() {
        if (connection != null && connection.isConnected()) {
            try {
                for (String jid : incomingSubscriptionRequests) {
                    Presence subscribed = new Presence(Presence.Type.subscribed);
                    subscribed.setTo(JidCreate.bareFrom(jid));
                    connection.sendStanza(subscribed);

                    // Send a "subscribe" presence to the contact as well
                    Presence subscribe = new Presence(Presence.Type.subscribe);
                    subscribe.setTo(JidCreate.bareFrom(jid));
                    connection.sendStanza(subscribe);
                }
                return true;
            } catch (SmackException | InterruptedException | IOException ex) {
                ex.printStackTrace();
                System.err.println("Error while accepting requests: " + ex.getMessage());
            }
        }
        return false;
    }

    /** 
     * @return List<String>
     */
    public List<String> getSubscriptionRequests() { return incomingSubscriptionRequests; }

//=================================================================================================NOTIFICATIONS=================================================================================================


    public void sendConnectionNotificationToFriends() {
        List<String> friends = getContacts(); // Obtener la lista de amigos
        String notification = username + " has just connected.";

        for (String friend : friends) {
            try {
                EntityBareJid jid = JidCreate.entityBareFrom(friend);
                ChatManager chatManager = ChatManager.getInstanceFor(connection);
                Chat chat = chatManager.chatWith(jid);
                chat.send(notification);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Error encountered while sending message: " + ex.getMessage());
            }
        }
    }

    
    /** 
     * @param listener
     */
    public void registerMessageListener(StanzaListener listener) {
        if (connection != null && connection.isConnected()) {
            connection.addAsyncStanzaListener(listener, MessageTypeFilter.NORMAL);
        }
    }

//=================================================================================================GROUP CHAT=================================================================================================
    
    /** 
     * @param roomName
     */
    public void createGroupChatAndInvite(String roomName) {
        try {
            EntityBareJid roomJid = JidCreate.entityBareFrom(roomName + "@" + XMPP_SERVER);
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = manager.getMultiUserChat(roomJid);
            muc.create(Resourcepart.from(username)); // Usar el nombre de usuario actual como recurso
            muc.sendConfigurationForm(new Form(DataForm.Type.submit)); // Configuración predeterminada

            // Invita automáticamente al usuario actual
            muc.invite(JidCreate.entityBareFrom(username + "@" + DOMAIN), "¡Unámonos a esta sala!");

            System.out.println("Sala de chat grupal '" + roomName + "' creada y unida con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al crear la sala de chat grupal: " + e.getMessage());
        }
    }


    /** 
     * @param roomName
     * @param message
     */
    public void sendMessageToGroupChat(String roomName, String message) {
        EntityBareJid roomJID;
        try {
            roomJID = JidCreate.entityBareFrom(roomName + "@conference." + DOMAIN);
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(roomJID);

            Message msg = new Message(roomJID);
            msg.setBody(message);

            muc.sendMessage(msg);

            System.out.println("Mensaje enviado al grupo '" + roomName + "': " + message);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al enviar mensaje al grupo: " + e.getMessage());
        }
    }
    
    /** 
     * @param roomName
     */
    public void registerGroupMessageListener(String roomName) {
        EntityBareJid roomJID;
        try {
            roomJID = JidCreate.entityBareFrom(roomName + "@conference." + DOMAIN);
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(roomJID);

            muc.addMessageListener(message -> {
                if (message.getBody() != null) {
                    System.out.println("Mensaje del grupo '" + roomName + "': " + message.getBody());
                }
            });

            System.out.println("Escuchando mensajes en el grupo '" + roomName + "'.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al registrar el oyente de mensajes en el grupo: " + e.getMessage());
        }
    }

    
    /** 
     * @param roomName
     * @param userJID
     */
    public void inviteUserToGroupChat(String roomName, String userJID) {
        EntityBareJid roomJID;
        try {
            roomJID = JidCreate.entityBareFrom(roomName + "@conference." + DOMAIN);
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(roomJID);

            muc.invite(JidCreate.entityBareFrom(userJID), "¡Te invito a unirte a la sala de chat!");

            System.out.println("Invitación enviada a " + userJID + " para unirse a '" + roomName + "'.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al enviar invitación: " + e.getMessage());
        }
    }

    
    /** 
     * @param roomName
     */
    public void acceptInvitationAndJoinGroupChat(String roomName) {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        manager.addInvitationListener(new InvitationListener() {
            @Override
            public void invitationReceived(XMPPConnection conn, MultiUserChat room, EntityJid inviter, String reason, String password, Message message, MUCUser.Invite invitation) {
                try {
                    room.join(Resourcepart.from(username));
                    registerGroupMessageListener(roomName);
                    System.out.println("¡Te has unido a la sala de chat grupal '" + roomName + "'!");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error al unirse a la sala de chat grupal: " + e.getMessage());
                }
            }
        });
    }

}