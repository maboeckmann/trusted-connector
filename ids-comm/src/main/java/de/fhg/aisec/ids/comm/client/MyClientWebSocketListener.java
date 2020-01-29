package de.fhg.aisec.ids.comm.client;

import de.fhg.aisec.ids.comm.server.TestServer;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.eclipse.jetty.websocket.api.Session;

public class MyClientWebSocketListener implements WebSocketListener {

    @Override
    public void onOpen(WebSocket websocket) {
        System.out.println("Connection opened");
    }

    @Override
    public void onClose(WebSocket websocket, int code, String reason) {
        System.out.println("Connection closed");
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("Error occurred");
        t.printStackTrace();
    }

    @Override
    public void onBinaryFrame(byte[] payload, boolean finalFragment, int rsv) {
        System.out.println("Received binary input");
    }

    @Override
    public void onTextFrame(String payload, boolean finalFragment, int rsv) {
        String color;
        if(payload.length() > 20)
        {
            color = TestServer.ANSI_GREEN;
        }
        else
        {
            color = TestServer.ANSI_RED;
        }

        System.out.println(color + "Received text message response: \n" + payload + TestServer.ANSI_RESET);
    }
}
