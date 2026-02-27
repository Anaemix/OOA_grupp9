package client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import server.Gson_InstantTypeAdapter;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ClientWebSocketHandler extends WebSocketClient {
    //* List of listeners/Subscribers */
    private List<WebSocketEventListener> listeners = new ArrayList<>();
    
    /**
     * Constructor for ClientWebSocketHandler. Initializes the WebSocket client with the provided server URI.
     * @param serverUri The URI of the WebSocket server.
     */
    public ClientWebSocketHandler(URI serverUri) {
        super(serverUri);
    }
    
    /** 
     * Handles start of connection WebSocket server. Notifies listeners.
     * @param handshakedata The ServerHandshake object containing handshake data from the server.
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        listeners.forEach(WebSocketEventListener::onConnected);
    }
    
    /** 
     * Handles incoming messages from the WebSocket connection. Parses the message and notifies listeners.
     * @param message The raw message received from the WebSocket connection.
     */
    @Override
    public void onMessage(String message) {
        System.out.println("▪ws   ◀── type: incoming message, Body: " + message);

        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
        Message messageObj = gson.fromJson(jsonObject.get("message"), Message.class);
        if (messageObj.isImage()) {
            ConnectionHandler.Get_Image(messageObj.getText());
        }
        listeners.forEach(listener -> listener.onMessageReceived(messageObj));
    }
    
    /**
     * Sends a message to the server to enter a specific chat room.
     * @param chatName The name of the chat room to enter.
     */
    public void enterChat(String chatName) {
        String jsonMessage = "{\"t\":\"enterchat\", \"chat\":\"" + chatName + "\"}";
        System.out.println("▪ws   ──▶ type: enterchat, Body: " + jsonMessage);
        this.send(jsonMessage);
    }

    /**
     * Sends a login message to the server with the provided username.
     * @param username The username to be sent to the server for login.
     */
    public void login(String username) {
        String jsonMessage = "{\"t\":\"connect\", \"name\":\"" + username + "\"}";
        System.out.println("▪ws   ──▶ type: connect, Body: " + jsonMessage);
        this.send(jsonMessage);
    }

    /**
     * Sends a message to the server within a specific chat.
     * @param message The Message object to be sent.
     * @param chatName The name of the chat to send the message to.
     */
    public void sendMessageToServer(Message message, String chatName) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
        JsonObject jsonMessage = new JsonObject();
        jsonMessage.addProperty("t", "send");
        jsonMessage.addProperty("chat", chatName);
        jsonMessage.add("message", gson.toJsonTree(message, Message.class));
        String jsonString = gson.toJson(jsonMessage);
        System.out.println("▪ws   ──▶ type: send, Body: " + jsonString);
        this.send(jsonString);
    }

    /**
     * Notifies listeners of disconnection when the WebSocket connection is closed.
     * @param code The status code representing the reason for closure.
     * @param reason A string describing the reason for closure.
     * @param remote A boolean indicating whether the closure was initiated by the remote host.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed");
        listeners.forEach(WebSocketEventListener::onDisconnected);
    }
    
    /**
    * Notifies listeners of any errors that occur in the WebSocket connection.
    * @param ex The Exception object representing the error that occurred.
    */
    @Override
    public void onError(Exception ex) {
        System.out.println("Error: " + ex.getMessage());
        listeners.forEach(listener -> listener.onError(ex.getMessage()));
    }
    
    /**
     * Adds a WebSocketEventListener to the list of listeners.
     * @param listener The WebSocketEventListener to be added to the array of listeners.
     */
    public void addListener(WebSocketEventListener listener) {
        listeners.add(listener);
    }
    /**
     * Removes a WebSocketEventListener from the list of listeners.
     * @param listener The WebSocketEventListener to be removed from the array of listeners.
     */
    public void removeListener(WebSocketEventListener listener) {
        listeners.remove(listener);
    }
}