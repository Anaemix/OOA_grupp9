
package server;

import client.Message;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.Set;
import java.time.Instant;

public class WebsocketHandler extends WebSocketServer {
    private HashMap<InetSocketAddress, String> connectionsToUsers;
    private final DatabaseHandler db;
    private UserChatMap userChatMap;

    public WebsocketHandler(int port, DatabaseHandler databaseHandler) {
        super(new InetSocketAddress(port));
        this.db = databaseHandler;
        connectionsToUsers = new HashMap<>();
        userChatMap = new UserChatMap();
        
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake){
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String incoming) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
        JsonObject j = gson.fromJson(incoming, JsonObject.class);

        if (
            connectionsToUsers.containsKey(conn.getRemoteSocketAddress()) 
            && j.get("t").getAsString().equals("send")) {

            Message message = gson.fromJson(j.get("message"), Message.class);
            String chat  = j.get("chat").getAsString();
            db.addMessage(chat, message);
            
            JsonObject update = new JsonObject();
            update.add("chat", j.get("chat"));
            update.add("message", j.get("message"));
            String SerializedUpdate = gson.toJson(update);

            Set<String> updateUsers = userChatMap.getUsers(chat);
            //For each connected client, if its corresponding user is in the updateUsers Set, send the message.
            for (WebSocket client : getConnections()) {
                if (updateUsers.contains(connectionsToUsers.get(client.getRemoteSocketAddress())))
                    client.send(SerializedUpdate);
            }

        } else if (
            connectionsToUsers.containsKey(conn.getRemoteSocketAddress()) 
            && j.get("t").getAsString().equals("enterchat")) {
            
            String user = connectionsToUsers.get(conn.getRemoteSocketAddress());
            String chat = j.get("chat").getAsString();
            userChatMap.put(user , chat);
            System.out.println(String.format("User \"%s\" entered chat \"%s\"", user, chat));

        } else if (
            j.get("t").getAsString().equals("connect")) {
            connectionsToUsers.put(conn.getRemoteSocketAddress(), j.get("user").getAsString());
            System.out.println(String.format("%s connected as %s", conn.getRemoteSocketAddress().toString(), j.get("user").getAsString()));
            
        }
        //for (WebSocket client : getConnections()) {
        //    conn.send(String.format("port: %d", client.getRemoteSocketAddress().getPort()));
        //}
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote){
        
        if (connectionsToUsers.containsKey(conn.getRemoteSocketAddress())) {
            System.out.println(connectionsToUsers.get(conn.getRemoteSocketAddress()) + " disconnected!");
            connectionsToUsers.remove(conn.getRemoteSocketAddress());
        } else {
            System.out.println(conn.getRemoteSocketAddress() + " disconnected!");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
    }



}