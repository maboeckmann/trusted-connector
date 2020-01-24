package de.fhg.aisec.ids.comm.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.concurrent.atomic.AtomicReference;

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
            if(msg.length() < 10) { return; }
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
            //query = "query={" + query + "}";
            System.out.println("Received query: " + query);
            //String result = server.getRepositoryFacade().query(query, tupleQueryResult -> {
            //    OutputStream resultOut = new ByteArrayOutputStream();
            //    QueryResults.report(tupleQueryResult, new SPARQLResultsXMLWriter(resultOut));
            //    return resultOut.toString();
            //});
            //System.out.println("Result: " + result);
            //session.getRemote().sendString("Query executed. Result: " + result);

            RepositoryConnection connection = server.getRepositoryFacade().getRepositoryConnection();
//            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL,"SELECT ?s ?p ?o WHERE { ?s ?p ?o . }");
            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
            StringBuilder builder = new StringBuilder();
            try (TupleQueryResult tupleQueryResult = tupleQuery.evaluate())
            {
                while (tupleQueryResult.hasNext())
                {
                    for(Binding b : tupleQueryResult.next())
                    {
                        builder.append(b.getValue()).append(" ");
                    }
                    builder.deleteCharAt(builder.length()-1).append("\n");
                }
                String response = builder.toString();
                while(response.length() > 10000)
                {
                    session.getRemote().sendString(response.substring(0, 10000));
                    response = response.substring(10000);
                }
                session.getRemote().sendString(response);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyClosed(IdscpServerSocket idscpServerSocket) {
        System.out.println("Connection closed.");
    }
}
