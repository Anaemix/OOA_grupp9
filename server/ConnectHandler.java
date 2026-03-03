package server;

import client.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.time.Instant;


/**
 * This http handler class handles the joining/connecting to a chat room.
 */
public class ConnectHandler implements HttpHandler {
    /** The handler used for database persistence. */
    private final DatabaseHandler db;
    //** websockethandler for updating userlists */
    private WebsocketHandler wsHandler;
    /** Gson object used for deserialization of json. */
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();

    /**
     * Constructor 
     * @param databaseHandler handles the database connection, writing/reading.
     * @param websocketHandler handles the websocket connections.
     */
    public ConnectHandler(DatabaseHandler databaseHandler, WebsocketHandler websocketHandler) {
        this.db = databaseHandler;
        this.wsHandler = websocketHandler;
    }

    /**
     * This handles the http connect request depending on the Request type. 
     * Will never return any data to the requestee and will respond with statuscodes <br>
     * -200 OK <br>
     * -400 Bad Request, if an exception was raised in the json parsing or addition of the user in the database <br>
     * -405 Method Not Allowed, if POST request method was not used <br>
     * @param httpexchange http exchange to be handled by the function
     */
    public void handle(HttpExchange httpexchange) throws IOException {
        if (httpexchange.getRequestMethod().equalsIgnoreCase("POST")) {
            try {
                InputStream is = httpexchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                JsonObject jsonobject = gson.fromJson(body, JsonObject.class);

                User user = gson.fromJson(jsonobject.getAsJsonObject("user"), User.class);
                String chat = jsonobject.get("chat").getAsString();
                
                db.addUser(user);
                db.addChat(chat);
                db.addUserToChat(user, chat);

                System.out.println(String.format("User %s added to chat %s", user.getName(), chat));
                httpexchange.sendResponseHeaders(200, -1);
                wsHandler.send_chatlist(chat);
            } catch (Exception e) {
                e.printStackTrace();
                httpexchange.sendResponseHeaders(400, -1);
            }
        } else {
            httpexchange.sendResponseHeaders(405, -1);
        }
    }
}
