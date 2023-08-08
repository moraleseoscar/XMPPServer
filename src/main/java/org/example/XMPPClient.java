package org.example;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;


import java.io.IOException;

public class XMPPClient {
    private static final String XMPP_SERVER = "alumchat.xyz";
    private static final int PORT = 5222; // Puerto predeterminado para XMPP
    private static final String DOMAIN = "alumchat.xyz"; // Agrega el dominio de tu XMPP server

    public static void main(String[] args) {
        String username = "estrada20565";
        String password = "admin";

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
            AbstractXMPPConnection connection = new XMPPTCPConnection(config);

            connection.connect();
            System.out.println("Conexión exitosa al servidor XMPP.");
            connection.login(username, password);

            System.out.println("Bien logueado.");

            // Aquí puedes realizar otras operaciones con el servidor XMPP

            connection.disconnect();

        } catch (XmppStringprepException  e) {
            e.printStackTrace();
            System.err.println("Error al conectarse al servidor XMPP: " + e.getMessage());
        } catch (SmackException | IOException | XMPPException | InterruptedException ex){
            ex.printStackTrace();
            System.err.println("Error al conectarse al servidor XMPP: " + ex.getMessage());
        }
    }

}
