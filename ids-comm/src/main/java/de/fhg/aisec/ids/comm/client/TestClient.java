package de.fhg.aisec.ids.comm.client;

import org.asynchttpclient.ws.WebSocket;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

public class TestClient {

    private String identifier;
    private String host;
    private int port;
    WebSocket wsClient;
    IdscpClient idscpClient;
    ServerSocket serverSocket;
    static String response;
    //MyClientWebSocketListener listener;

    private void init(String identifier)
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
        if(wsClient != null && wsClient.isOpen())
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
        if(wsClient != null) {
            wsClient.sendTextFrame("IDENTITY " + identifier + " %$% " + query);
        }
    }

    public void startWebSocket() throws IOException {
        serverSocket = new ServerSocket(1234);
    }

    public static void main(String[] args) {
        try {
            String path = Path.of("/home/" + System.getProperty("user.name") + "/Desktop/").toString();
            TestClient testClient = new TestClient();
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            testClient.setTarget(host, port);
            testClient.init(Files.lines(Path.of(path + "/identity.txt"), StandardCharsets.UTF_8).findFirst().get());
            //testClient.init(args[0].charAt(0));
            StringBuilder contentBuilder = new StringBuilder();
            Stream<String> stream = Files.lines( Path.of(path + "/trusted-connector-query.sparql"), StandardCharsets.UTF_8);
            stream.forEach(s -> contentBuilder.append(s).append("\n"));



            String gets = "";
            testClient.startWebSocket();
            while(true) {
                try {
                    // wait for a connection
                    Socket remote = testClient.serverSocket.accept();
                    // remote is now the connected socket
                    //System.out.println("Connection, sending data.");
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            remote.getInputStream()));
                    PrintWriter out = new PrintWriter(remote.getOutputStream());

                    String str = ".";

                    while (!str.equals("")) {
                        str = in.readLine();
                        if (str.contains("GET")){
                            gets = str;
                            break;
                        }
                    }
                    gets = gets.substring(gets.indexOf("id=") + 3);
                    gets = gets.substring(0, gets.indexOf(" HTTP"));
                    String originalQuery = contentBuilder.toString();
                    response = null;
                    testClient.sendQuery(originalQuery.replace("$id$", "LP FFM" + gets));
                    for(int i = 0; i < 100; i++) //wait up to 10 seconds
                    {
                        if(response != null)
                        {
                            break;
                        }
                        Thread.sleep(100);
                    }
//                    testClient.sendQuery(contentBuilder.toString());
                    if(response.equals("Rejected")) {
                        out.println("HTTP/1.0 403 FORBIDDEN");
                    }
                    else
                    {
                        out.println("HTTP/1.0 200 OK");
                        out.println("Content-Type: text/html");
                        out.println("");
                        if (response == null)
                        {
                            out.println("No response received from server.");
                        }
                        else {
                            out.print(response);
                        }
                    }
/*                    String method = "get";
                    out.print("<html><form method="+method+">");
                    out.print("<textarea name=we></textarea></br>");
                    out.print("<input type=text name=a><input type=submit></form></html>");
                    out.println(gets);*/
                    out.flush();

                    remote.close();
                    in.close();

                } catch (Exception ignored) {
//                    System.out.println("Error: " + e);
                }
            }
/*
            try (Stream<String> stream = Files.lines( Path.of(path + "/trusted-connector-query.sparql"), StandardCharsets.UTF_8))
            {
                stream.forEach(s -> contentBuilder.append(s).append("\n"));
                testClient.sendQuery(contentBuilder.toString());
            }
            catch (IOException | NullPointerException e)
            {
                e.printStackTrace();
            }
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override public void run()
                        {
                            System.exit(0);
                        }
                    }, 3000
            );

*/
//            testClient.sendQuery("SELECT * { ?s ?p ?o . }");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
