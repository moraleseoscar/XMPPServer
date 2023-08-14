package org.example;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jivesoftware.smack.packet.Presence;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private List<String[]> contacts;

    public XMPPClient() {
        connect();
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
            System.out.println("Conexión exitosa al servidor XMPP.");
            return true;
            // Aquí puedes realizar otras operaciones con el servidor XMPP

        } catch (XmppStringprepException e) {
            e.printStackTrace();
            System.err.println("Error al conectarse al servidor XMPP: " + e.getMessage());
            return false;
        } catch (SmackException | IOException | XMPPException | InterruptedException ex) {
            ex.printStackTrace();
            System.err.println("Error al conectarse al servidor XMPP: " + ex.getMessage());
            return false;
        }
    }

    public boolean login(String username, String password) {
        try {
            connection.login(username, password);
            this.username = username;
            this.password = password;
            System.out.println("Bien logueado.");
            return true;
        }catch (SmackException | IOException | XMPPException | InterruptedException ex) {
            ex.printStackTrace();
            System.err.println("Error al conectarse al servidor XMPP: " + ex.getMessage());
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
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all); // Opcional: aceptar automáticamente solicitudes de suscripción

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
                System.err.println("Error al agregar contacto: " + ex.getMessage());
            }
        }
        return false;
    }

    public boolean acceptContactRequest(String contactJID) {
        if (connection != null && connection.isConnected()) {
            try {
                Roster roster = Roster.getInstanceFor(connection);
                Presence subscribed = new Presence(Presence.Type.subscribed);
                subscribed.setTo(JidCreate.bareFrom(contactJID));
                connection.sendStanza(subscribed);
                return true;
            } catch (SmackException | InterruptedException | IOException ex) {
                ex.printStackTrace();
                System.err.println("Error al aceptar solicitud de contacto: " + ex.getMessage());
            }
        }
        return false;
    }

}
