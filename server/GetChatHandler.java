package server;

import client.Chat;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Instant;

/**
 * This http handler class handles the joining/connecting to a chat room.
 * 
 * @author Henning
 * @version 0.1
 */
public class GetChatHandler implements HttpHandler {
    /** The handler used for database persistence. */
    private final DatabaseHandler db;
    /** Gson object used for deserialization of json. */
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();

    /**
     * Constructor 
     * @param databaseHandler handles the database connection, writing/reading.
     */
    public GetChatHandler(DatabaseHandler databaseHandler) {
        this.db = databaseHandler;
    }
    /**
     * This handles the http request. 
     * Responds with a serialized json string of a Chat object
     * Will respond with statuscodes <br>
     * -200 OK <br>
     * -400 Bad Request, if an exception was raised in the json parsing or addition of the user in the database <br>
     * -405 Method Not Allowed, if POST request method was not used <br>
     * Test with "curl http://fjenhh.me:2345/get_chat/{chatname}"
     * @param httpexchange http exchange to be handled by the function
     */
    public void handle(HttpExchange httpexchange) throws IOException {
        if (httpexchange.getRequestMethod().equalsIgnoreCase("GET")) {
            try {
                String chat_name = httpexchange.getRequestURI().getPath().replace("/get_chat/", "");
                Chat c = db.getChat(chat_name);
                System.out.println(c.getUsers());
                String response = gson.toJson(c);

                httpexchange.getResponseHeaders().add("Content-Type", "application/json");
                httpexchange.sendResponseHeaders(200, response.getBytes().length);
                
                try (OutputStream os = httpexchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                httpexchange.sendResponseHeaders(400, -1);
            }
        } else {
            httpexchange.sendResponseHeaders(405, -1);
        }
    }
}
