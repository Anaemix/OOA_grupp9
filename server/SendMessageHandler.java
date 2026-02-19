package server;

import client.Message;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.Set;

/**
 * This http handler class handles sending messages to a chat.
 * This class and its functions are considered deprecated. Instead use {@link server.WebsocketHandler WebsocketHandler} connection for sending messages. 
 * @author Henning
 * @version 0.1
 */
@Deprecated
public class SendMessageHandler implements HttpHandler {
    /** The handler used for database persistence. */
    private final DatabaseHandler db;
    /** Gson object used for deserialization of json. */
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();

    /**
     * Constructor 
     * @param databaseHandler handles the database connection, writing/reading.
     */
    @Deprecated
    public SendMessageHandler(DatabaseHandler databaseHandler) {
        this.db = databaseHandler;
    }

    /**
     * **************************************<br>
     * ************Deprecated**************<br>
     * **{@link server.WebsocketHandler use WebsocketHandler instead}**<br>
     * **************************************<br>
     * Http handler for sending messages to a chat
     * Never Responds with any data. <br>
     * Will set statuscodes: <br>
     * -200 OK <br>
     * -400 Bad Request, if an exception was raised in the json parsing or addition of the user in the database <br>
     * -405 Method Not Allowed, if POST request method was not used <br>
     * Example:
     * curl --header "Content-type: application/json" --request POST -data '{"chat": "Hennings Privata chat", "message": {"text": "Not so secret message :)", "user": {"name": "Coola Henning"}, "time": "2026-02-04T11:14:05Z"}}' http://fjenhh.me:2345/send_message
     * @param httpexchange http exchange to be handled by the function
     */
    @Deprecated
    public void handle(HttpExchange httpexchange) throws IOException {
        if (httpexchange.getRequestMethod().equalsIgnoreCase("POST")) {
            String response = "0";

            try {
                InputStream is = httpexchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                JsonObject jsonobject = gson.fromJson(body, JsonObject.class);
                Set<String> keys = jsonobject.keySet();
                if (keys.contains("chat") && keys.contains("message")) {
                    String chat_name = jsonobject.get("chat").getAsString();
                    Message message = gson.fromJson(jsonobject.getAsJsonObject("message"), Message.class);

                    db.addMessage(chat_name, message);

                    response = "1";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            httpexchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            httpexchange.sendResponseHeaders(200, responseBytes.length);
            
            try (OutputStream os = httpexchange.getResponseBody()) {
                os.write(responseBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            httpexchange.sendResponseHeaders(451, -1);
        }

    }
}
