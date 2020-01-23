package de.fhg.aisec.ids.comm.server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class MySocketListener implements SocketListener {
    @Override
    public void onMessage(Session session, byte[] message) {
        try {
            String msg = new String(message);
            String identity = msg.substring(9, 10);
            if (identity.toLowerCase().equals("a")) {
                System.out.println("Received a message from Connector A - Access granted.");
            } else if (identity.toLowerCase().equals("b")) {
                System.out.println("Received a message from Connector B - Access denied.");
                session.getRemote().sendString("Rejected");
                return;
            } else {
                System.out.println("Received a message from unknown Connector - Access denied.");
                return;
            }

            String query = msg.substring(msg.indexOf("%$%") + 4);
            System.out.println("Received query: " + query);
            session.getRemote().sendString("Query executed");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyClosed(IdscpServerSocket idscpServerSocket) {
        System.out.println("Connection closed.");
    }
}
