package de.fhg.aisec.ids.comm.client;

import org.asynchttpclient.ws.WebSocket;
import org.eclipse.rdf4j.query.algebra.In;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;

public class TestClient {

    private char identifier;
    private String host;
    private int port;
    WebSocket wsClient;
    IdscpClient idscpClient;
    //MyClientWebSocketListener listener;

    private void init(char identifier)
    {
        try {
            //listener = new MyClientWebSocketListener();

            this.identifier = identifier;
            idscpClient =
                    new IdscpClient().config(new ClientConfiguration.Builder()
                            .setSha256CertificateHashes(
                                    Collections.singletonList(
                                            DatatypeConverter.parseHexBinary(
                                                    "4439DA49F320E3786319A5CF8D69F3A0831C4801B5CE3A14570EA84E0ECD82B0")))
                            .build());
            wsClient = idscpClient.connect(host, port);
            //wsClient.addWebSocketListener(listener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void setTarget(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public void refreshClient()
    {
        if(wsClient.isOpen())
        {
            return;
        }
        try {
            wsClient = idscpClient.connect(host, port);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendQuery(String query)
    {
        refreshClient();
        wsClient.sendTextFrame("IDENTITY " + identifier + " %$% " + query);
    }

    public static void main(String[] args) {
        try {
            TestClient testClient = new TestClient();
            String host = args[1];
            int port = Integer.parseInt(args[2]);
            testClient.setTarget(host, port);
            testClient.init(args[0].charAt(0));
            StringBuilder contentBuilder = new StringBuilder();
            //String path = "/home/" + System.getProperty("user.name") + "/Desktop/trusted-connector-query.sparql";
            try (Stream<String> stream = Files.lines( Paths.get("/home/" + System.getProperty("user.name") + "/Desktop/trusted-connector-query.sparql"), StandardCharsets.UTF_8))
            {
                stream.forEach(s -> contentBuilder.append(s).append("\n"));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            testClient.sendQuery(contentBuilder.toString());
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override public void run()
                        {
                            System.exit(0);
                        }
                    }, 3000
            );
//            testClient.sendQuery("SELECT * { ?s ?p ?o . }");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
