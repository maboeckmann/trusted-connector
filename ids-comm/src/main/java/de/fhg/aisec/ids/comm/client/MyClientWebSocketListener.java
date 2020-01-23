package de.fhg.aisec.ids.comm.client;

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
        System.out.println("Received text message response: " + payload);
    }
}
