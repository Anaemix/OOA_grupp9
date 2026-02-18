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

public class SendMessageHandler implements HttpHandler {
    private final DatabaseHandler db;
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
    
    public SendMessageHandler(DatabaseHandler databaseHandler) {
        this.db = databaseHandler;
    }

    /**
     * Http handler for sending messages to a chat
     * 
     * Example:
     * curl --header "Content-type: application/json" \
     * --request POST \
     * --data '{"chat": "Hennings Privata chat", "message": {"text": "Hämligt meddelande :)", "user": {"name": "Coola Henning", "id": "1"}, "time": "2026-02-04T11:14:05Z"}}' \
     * http://localhost:2345/send_message
     * 
     */
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
