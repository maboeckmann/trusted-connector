package de.fhg.aisec.ids.comm.client;

import org.asynchttpclient.ws.WebSocket;

import javax.xml.bind.DatatypeConverter;
import java.util.Collections;

public class TestClient {

    private char identifier;
    private String host;
    private int port;
    WebSocket wsClient;
    IdscpClient idscpClient;
    MyClientWebSocketListener listener;

    private void init(char identifier)
    {
        try {
            listener = new MyClientWebSocketListener();

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
            testClient.setTarget("localhost", 8081);
            testClient.init(args[0].charAt(0)); //TODO test
            testClient.sendQuery("SELECT some stuff");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
