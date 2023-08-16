package org.example;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.awt.desktop.SystemSleepEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMPPClient {
    //Datos predeterminados
    private static final String XMPP_SERVER = "alumchat.xyz";
    private static final int PORT = 5222;
    private static final String DOMAIN = "alumchat.xyz";

    //Atributos para editar.
    private String username;
    private String password;
    private AbstractXMPPConnection connection;
    private Roster roster;
    private List<String> incomingSubscriptionRequests = new ArrayList<>();
    private Map<String, List<String>> messageHistory;

    public XMPPClient(Map<String, List<String>> messageHistory) {
        connect();
        this.messageHistory = messageHistory;
    }

    public Map<String, List<String>> getMessageHistory(){
        return this.messageHistory;
    }

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

    public boolean login(String username, String password) {
        try {
            connection.login(username, password);
            this.username = username;
            this.password = password;
            return true;
        }catch (SmackException | IOException | XMPPException | InterruptedException ex) {
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

    //==================================================================================MENSAJES=====================================================
    public boolean sendMessage(String contactJID, String messageBody) {
        if (connection != null && connection.isConnected()) {
            try {
                EntityBareJid jid = JidCreate.entityBareFrom(contactJID);
                ChatManager chatManager = ChatManager.getInstanceFor(connection);
                Chat chat = chatManager.chatWith(jid);

                chat.send(messageBody);

                String key = getChatKey(username, contactJID.replace("@alumchat.xyz", "")); // Obtener la clave del chat
                String formattedMessage = username + ": " + messageBody;
                addMessageToChatHistory(key, formattedMessage);

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Error encountered while sending message: " + ex.getMessage());
            }
        }
        return false;
    }


    private String getChatKey(String user1, String user2) {
        if (user1.compareTo(user2) < 0) {
            return user1 + " " + user2;
        } else {
            return user2 + " " + user1;
        }
    }

    public void addMessageToChatHistory(String key, String message) {
        List<String> chatHistory = messageHistory.getOrDefault(key, new ArrayList<>());
        chatHistory.add(message);
        messageHistory.put(key, chatHistory);
    }

    public List<String> getChatHistory(String contactJID) {
        String key = getChatKey(username, contactJID.replace("@alumchat.xyz", ""));
        return messageHistory.getOrDefault(key, new ArrayList<>());
    }

    //==================================================================================SUSCRIPCIONES=====================================================
    public List<String> getSubscriptionRequests() {
        return incomingSubscriptionRequests;
    }

    public boolean acceptAllRequests(){
        if (connection != null && connection.isConnected()) {
            try {
                for (String jid : incomingSubscriptionRequests) {
                    Roster roster = Roster.getInstanceFor(connection);
                    Presence subscribed = new Presence(Presence.Type.subscribed);
                    subscribed.setTo(JidCreate.bareFrom(jid));
                    connection.sendStanza(subscribed);
                    return true;
                }
            }catch (SmackException | InterruptedException | IOException ex) {
                ex.printStackTrace();
                System.err.println("Error while accepting requests: " + ex.getMessage());
            }
        }
        return false;
    }

}