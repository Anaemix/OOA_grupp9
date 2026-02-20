
package server;

import client.Message;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.Set;
import java.time.Instant;

/**
 * This class handles the websocket connections to the server.
 * 
 * @author Henning
 * @version 0.1
 */
public class WebsocketHandler extends WebSocketServer {
    /** Hashmap that maps each connection address to a user to allow for simpler communication in the future */
    private HashMap<InetSocketAddress, String> connToUsers;
    /** The handler used for database persistence. */
    private final DatabaseHandler db;
    /** Map datatype for bidirectional access to which users are connected to a chat or which chat a specific user is connected to */
    private UserChatMap userChatMap;

    /**
     * Constructor
     * 
     * @param port Port to be used for the websocket server
     * @param databaseHandler handles the database connection, writing/reading.
     */
    public WebsocketHandler(int port, DatabaseHandler databaseHandler) {
        super(new InetSocketAddress(port));
        this.db = databaseHandler;
        connToUsers = new HashMap<>();
        userChatMap = new UserChatMap();
        
    }

    /** 
     * Prints out whenever someone connects to the websocket
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake){
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    /**
     * Triggers on message received by a client. Only accepts Json string formatted like <br>
     * {"t": "send", "message": {"user": {"name": "dummyname"}, "text": "dummymessagetext", "time": "YYYY-MM-DDTHH:MM:SSZ"}} <br>
     * {"t": "enterchat", "chat": "chat to enter"} <br>
     * {"t": "connect", "user": "username to bind to the connection address+port"}
     * @param conn connection data
     * @param incoming message from the client in form of a string
     */
    @Override
    public void onMessage(WebSocket conn, String incoming) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
        JsonObject j = gson.fromJson(incoming, JsonObject.class);
        String messageType = j.get("t").getAsString();
        if (
        connToUsers.containsKey(conn.getRemoteSocketAddress()) && messageType.equals("send"))
            send_message(gson, j);
        else if (
        connToUsers.containsKey(conn.getRemoteSocketAddress()) && messageType.equals("enterchat"))
            enterChat(connToUsers.get(conn.getRemoteSocketAddress()), j.get("chat").getAsString());
        else if (messageType.equals("connect"))
            connect(conn.getRemoteSocketAddress(), j.get("user").getAsString());
    }

    /**
     * Maps address with a user
     * @param address address
     * @param username user
     */
    private void connect(InetSocketAddress address, String username) {
        connToUsers.put(address, username);
        System.out.println(String.format("%s connected as %s", address, username));
    }

    /**
     * Maps user with a chat
     * @param user user
     * @param chatname chat that the user has entered
     */
    private void enterChat(String user, String chatname) {
        userChatMap.put(user , chatname);
        System.out.println(String.format("User \"%s\" entered chat \"%s\"", user, chatname));
    }

    /**
     * Sends a message to a chat. Then sends the message to all users currently active in the chat.
     * 
     * @param gson Gson object for serialization and deserialization
     * @param jsonobject json object of the message sent to the server.
     */
    private void send_message(Gson gson, JsonObject jsonobject) {
        Message message = gson.fromJson(jsonobject.get("message"), Message.class);
        String chat  = jsonobject.get("chat").getAsString();
        db.addMessage(chat, message);
        
        JsonObject update = new JsonObject();
        update.add("chat", jsonobject.get("chat"));
        update.add("message", jsonobject.get("message"));
        String SerializedUpdate = gson.toJson(update);

        Set<String> updateUsers = userChatMap.getUsers(chat);
        //For each connected client, if its corresponding user is in the updateUsers Set, send the message.
        for (WebSocket client : getConnections()) {
            if (updateUsers.contains(connToUsers.get(client.getRemoteSocketAddress())))
                client.send(SerializedUpdate);
        }
    }

    /**
     * Triggered when a connection is ended
     * 
     * @param conn Websocket connection object
     * @param code status code
     * @param reason reason string
     * @param remote remote close
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote){
        
        if (connToUsers.containsKey(conn.getRemoteSocketAddress())) {
            System.out.println(connToUsers.get(conn.getRemoteSocketAddress()) + " disconnected!");
            connToUsers.remove(conn.getRemoteSocketAddress());
        } else {
            System.out.println(conn.getRemoteSocketAddress() + " disconnected!");
        }
    }

    /**
     * Triggered on error when running the websocket server
     * 
     * @param conn Websocket connection object
     * @param ex Exception object triggered
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    /**  */
    @Override
    public void onStart() {
    }



}