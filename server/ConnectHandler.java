package server;

import client.User;

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

public class ConnectHandler implements HttpHandler {
    private final DatabaseHandler db;
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
    
    public ConnectHandler(DatabaseHandler databaseHandler) {
        this.db = databaseHandler;
    }

    public void handle(HttpExchange httpexchange) throws IOException {
        if (httpexchange.getRequestMethod().equalsIgnoreCase("POST")) {
            String response = "0";

            try {
                InputStream is = httpexchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                JsonObject jsonobject = gson.fromJson(body, JsonObject.class);
                Set<String> keys = jsonobject.keySet();


                if (keys.contains("user") && keys.contains("chat")) {
                    User user = gson.fromJson(jsonobject.getAsJsonObject("user"), User.class);
                    String chat = jsonobject.get("chat").getAsString();
                    
                    db.addUser(user);
                    db.addChat(chat);
                    db.addUserToChat(user, chat);

                    response = "true";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            httpexchange.getResponseHeaders().add("Content-Type", "application/json");
            httpexchange.sendResponseHeaders(200, response.getBytes().length);
            
            try (OutputStream os = httpexchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            httpexchange.sendResponseHeaders(405, -1);
        }
    }
}
