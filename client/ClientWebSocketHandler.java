package client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ClientWebSocketHandler extends WebSocketClient {
    private List<WebSocketEventListener> listeners = new ArrayList<>();
    
    public ClientWebSocketHandler(URI serverUri) {
        super(serverUri);
    }
    
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        listeners.forEach(WebSocketEventListener::onConnected);
    }
    
    @Override
    public void onMessage(String message) {
        System.out.println("▪ws   ◀── type: incoming message, Body: " + message);
        // Trigger event for all listeners
        listeners.forEach(listener -> listener.onMessageReceived(message));
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed");
        listeners.forEach(WebSocketEventListener::onDisconnected);
    }
    
    @Override
    public void onError(Exception ex) {
        System.out.println("Error: " + ex.getMessage());
        listeners.forEach(listener -> listener.onError(ex.getMessage()));
    }
    
    // Add/remove listeners
    public void addListener(WebSocketEventListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(WebSocketEventListener listener) {
        listeners.remove(listener);
    }
    
    // Send messages
    public void sendMessageToServer(String message) {
        this.send(message);
    }
}