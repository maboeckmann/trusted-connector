package de.fhg.aisec.ids.comm.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MySocketListener implements SocketListener {
    private static TestServer server;
    public MySocketListener(TestServer server)
    {
        MySocketListener.server = server;
    }
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
            String result = server.getRepositoryFacade().query(query, tupleQueryResult -> {
                OutputStream resultOut = new ByteArrayOutputStream();
                QueryResults.report(tupleQueryResult, new SPARQLResultsXMLWriter(resultOut));
                return resultOut.toString();
            });
            session.getRemote().sendString("Query executed. Result: " + result);
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
