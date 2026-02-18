package server;

import client.Chat;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Instant;

public class GetChatHandler implements HttpHandler {
    private final DatabaseHandler db;
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
    
    public GetChatHandler(DatabaseHandler databaseHandler) {
        this.db = databaseHandler;
    }
    /**
     * Http handler for getting the contents of a chat. 
     * Request with "curl http://localhost:2345/get_chat/{chatname}"
     */
    public void handle(HttpExchange httpexchange) throws IOException {
        if (! httpexchange.getRequestMethod().equals("GET")) {
            httpexchange.sendResponseHeaders(451, -1);
            return;
        }

        String chat_name = httpexchange.getRequestURI().getPath().replace("/get_chat/", "");
        Chat c = db.getChat(chat_name);
        System.out.println(c.getUsers());
        String response = gson.toJson(c);

        httpexchange.getResponseHeaders().add("Content-Type", "application/json");
        httpexchange.sendResponseHeaders(200, response.getBytes().length);
        
        try (OutputStream os = httpexchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
