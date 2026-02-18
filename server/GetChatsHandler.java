package server;

import client.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Instant;

public class GetChatsHandler implements HttpHandler {
    private final DatabaseHandler db;
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
    
    public GetChatsHandler(DatabaseHandler databaseHandler) {
        this.db = databaseHandler;
    }

    public void handle(HttpExchange httpexchange) throws IOException {

        if (httpexchange.getRequestMethod().equals("GET")) {
            String username = httpexchange.getRequestURI().getPath().replace("/get_chats/", "");
            User user = new User(username);
            String response = gson.toJson(db.getAllChats(user));

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
