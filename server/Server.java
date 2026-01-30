package com.chatapp.server;
import com.chatapp.shared.Message;
import com.chatapp.shared.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class Server {

    public static void main(String[] args) throws IOException {
        int port = 2345;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0); 
        server.createContext("/get_chat", new GetChatHandler());
        server.setExecutor(null);
        server.start();
        System.out.println(String.format("Server up at port :%d", port));
    }

    /**
     * 
     */
    static class GetChatHandler implements HttpHandler {
        
        public void handle(HttpExchange httpexchange) throws IOException {
            if (! httpexchange.getRequestMethod().equals("GET")) {
                httpexchange.sendResponseHeaders(451, -1);
                return;
            }
            try {
            Gson gson = new Gson();
            String chat_name = httpexchange.getRequestURI().getPath().replace("/get_chat/", "");
            
            User u1 = new User("fb27a87b8a214", "HenningPenning");
            Message m1 = new Message("Hej där", LocalDateTime.now().toString(), u1);
            Message m2 = new Message("Är någon här?", LocalDateTime.now().toString(), u1);
            ArrayList<Message> a1 = new ArrayList<Message>();
            a1.add(m1);
            a1.add(m2);
            String messages = gson.toJson(a1);
            String response = String.format("{\"chat\": \"%s\", \"messages\": %s}", chat_name, messages); 
            System.out.println(response);
            httpexchange.getResponseHeaders().add("Content-Type", "application/json");
            httpexchange.sendResponseHeaders(200, response.getBytes().length);
            
            try (OutputStream os = httpexchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}