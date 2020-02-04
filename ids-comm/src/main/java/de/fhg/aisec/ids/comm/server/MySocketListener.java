package de.fhg.aisec.ids.comm.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.RepositoryConnection;


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
            if(!msg.contains("IDENTITY"))
            {
                return;
            }
            String identity = msg.substring(9, msg.indexOf("%$%"));
            if (identity.toLowerCase().contains("connector_a")) {
                System.out.println(TestServer.ANSI_GREEN + "Received a message - Access granted.");
            } else if (identity.toLowerCase().contains("connector_b")) {
                if(msg.contains("FILTER")) {
                    System.out.println("Message: " + msg);
                    System.out.println(TestServer.ANSI_RED + "Received a message - Access denied." + TestServer.ANSI_RESET);
                    session.getRemote().sendString("Rejected");
                    return;
                }
                else
                {
                    System.out.println(TestServer.ANSI_GREEN + "Received a message - Access granted.");
                }
            } else {
                System.out.println(TestServer.ANSI_RED + "Received a message - Access denied." + TestServer.ANSI_RESET);
                session.getRemote().sendString("Rejected");
                return;
            }

            String query = msg.substring(msg.indexOf("%$%") + 4);
            //query = "query={" + query + "}";
            if(query.length() > 100)
            {
                System.out.println("Received query: " + query.substring(0, 49) + TestServer.ANSI_RESET + " [...] " + TestServer.ANSI_GREEN + query.substring(query.length() - 50, query.length() - 1) + TestServer.ANSI_RESET);
            }
            else
            {
                System.out.println("Received query: " + query + TestServer.ANSI_RESET);
            }
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
                        builder.append(b.getName()).append("\t");
                        builder.append(b.getValue()).append("\n");
                    }
//                    builder.deleteCharAt(builder.length()-1).append("\n");
                    if(tupleQueryResult.hasNext())
                    {
                        builder.append("+++\n");
                    }
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
