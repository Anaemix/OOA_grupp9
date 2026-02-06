package server;

import client.Message;
import client.Chat;
import client.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.GsonBuilder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Server {
    private static DatabaseHandler db = new DatabaseHandler();
    public static void main(String[] args) throws IOException {
        int port = 2345;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0); 
        server.createContext("/get_chat", new GetChatHandler());
        server.createContext("/get_chats", new GetChatsHandler());
        server.createContext("/connect", new ConnectHandler());
        server.createContext("/disconnect", new DisconnectHandler());
        server.createContext("/send_message", new Send_MessageHandler());
        server.setExecutor(null);
        server.start();
        System.out.println(String.format("Server up at port :%d", port));
    }

    /**
     * 
     */
    static class GetChatsHandler implements HttpHandler {
        public void handle(HttpExchange httpexchange) throws IOException {
            if (httpexchange.getRequestMethod().equals("GET")) {
                Gson gson = new Gson();
                String input = httpexchange.getRequestURI().getPath().replace("/get_chats/", "");
                if(input.contains("/")) {
                    User user = new User(Integer.parseInt(input.split("/")[1]), input.split("/")[0]);
                    

                    // ----REPLACE----
                    String response = gson.toJson(db.getAllChats(user));
                    // ----REPLACE----

                    httpexchange.getResponseHeaders().add("Content-Type", "application/json");
                    httpexchange.sendResponseHeaders(200, response.getBytes().length);
                    
                    try (OutputStream os = httpexchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpexchange.sendResponseHeaders(400, -1);
                }

            } else {
                httpexchange.sendResponseHeaders(405, -1);
            }
        }
    }
    /**
     * Http handler for getting the contents of a chat. 
     * Request with "curl http://localhost:2345/get_chat/{chatname}"
     */
    static class GetChatHandler implements HttpHandler {
        
        public void handle(HttpExchange httpexchange) throws IOException {
            if (! httpexchange.getRequestMethod().equals("GET")) {
                httpexchange.sendResponseHeaders(451, -1);
                return;
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();

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
    /**
     * 
     */
    static class ConnectHandler implements HttpHandler {
        public void handle(HttpExchange httpexchange) throws IOException {
            if (httpexchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String response = "0";

                try {
                    InputStream is = httpexchange.getRequestBody();
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                    Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
                    JsonObject jsonobject = gson.fromJson(body, JsonObject.class);
                    Set keys = jsonobject.keySet();


                    if (keys.contains("user") && keys.contains("chat")) {
                        User user = gson.fromJson(jsonobject.getAsJsonObject("user"), User.class);
                        String chat = jsonobject.get("chat").getAsString();
                        // ----REPLACE----
                        db.addchat(chat);
                        db.adduser(user);
                        db.addUserToChat(user, chat);
                        // ----REPLACE----
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
        /**
     * 
     */
    static class DisconnectHandler implements HttpHandler {
        public void handle(HttpExchange httpexchange) throws IOException {
            if (httpexchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String response = "0";

                try {
                    InputStream is = httpexchange.getRequestBody();
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                    Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
                    JsonObject jsonobject = gson.fromJson(body, JsonObject.class);
                    Set keys = jsonobject.keySet();


                    if (keys.contains("user") && keys.contains("chat")) {
                        User user = gson.fromJson(jsonobject.getAsJsonObject("user"), User.class);
                        String chat = jsonobject.get("chat").getAsString();
                        // ----REPLACE----
                        //dummy_disconnect(user, chat);
                        db.removeUserFromChat(user, chat);
                        // ----REPLACE----
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
                httpexchange.sendResponseHeaders(451, -1);
            }
        }
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
     * Expects a json string following this json schema:
     * {
     *    "type": "object",
     *    "properties": {
     *        "chat": {
     *            "type": "string",
     *            "description": "chat_name"
     *        },
     *        "message": {
     *            "type": "object",
     *            "properties": {
     *                "text": {
     *                    "type": "string"
     *                },
     *                "user": {
     *                    "type": "object",
     *                    "properties": {
     *                        "name": {
     *                            "type": "string"
     *                        },
     *                        "id": {
     *                            "type": "string"
     *                        }
     *                    },
     *                    "required": [
     *                        "name",
     *                        "id"
     *                    ]
     *                },
     *                "time": {
     *                    "type": "string"
     *                }
     *            },
     *            "required": [
     *                "text",
     *                "user"
     *            ]
     *        }
     *    },
     *    "required": [
     *        "chat",
     *        "message"
     *    ]
     *}
     */
    static class Send_MessageHandler implements HttpHandler {
        public void handle(HttpExchange httpexchange) throws IOException {
            
            if (httpexchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String response = "0";

                try {
                    InputStream is = httpexchange.getRequestBody();
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                    Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
                    JsonObject jsonobject = gson.fromJson(body, JsonObject.class);
                    Set keys = jsonobject.keySet();
                    if (keys.contains("chat") && keys.contains("message")) {
                        String chat_name = jsonobject.get("chat").getAsString();
                        Message message = gson.fromJson(jsonobject.getAsJsonObject("message"), Message.class);

                        // ----REPLACE----
                        db.sendMessage(chat_name, message);
                        // ----REPLACE----

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
    private static Chat dummy_get_chat(String chat_name) {
        Chat dummy = new Chat(chat_name);
        User dummy_u1 = new User(1, "Coola Henning");
        User dummy_u2 = new User(2, "Najibis");
        User dummy_u3 = new User(3, "Mega Hugo");
        dummy.addUser(dummy_u1);
        dummy.addUser(dummy_u2);
        dummy.addUser(dummy_u3);
        dummy.addMessage(new Message("Alltså kolla Hennings Linked in story, hur kan man ens ha på sig den där tröjan? 😂", Instant.now(), dummy_u2));
        dummy.addMessage(new Message("Hahaha jag dör, han ser ut som en gammal morfar!", Instant.now().plus(8, ChronoUnit.SECONDS), dummy_u3));
        dummy.addMessage(new Message("Jag ser vad ni skriver, vi är i samma gruppchatt...", Instant.now().plus(289, ChronoUnit.SECONDS), dummy_u1));
        dummy.addMessage(new Message("OJ NEJ förlåt, trodde jag skrev i tråden med bara Najibbis!! 💀", Instant.now().plus(321, ChronoUnit.SECONDS), dummy_u2));
        dummy.addMessage(new Message("Kul", Instant.now().plus(344, ChronoUnit.SECONDS), dummy_u1));
        dummy.addMessage(new Message("Jepp, hehe", Instant.now().plus(834, ChronoUnit.SECONDS), dummy_u3));
        return dummy;
    }
    private static ArrayList<String> dummy_get_chats(User user) {
        ArrayList<String> dummy = new ArrayList<>();
        dummy.add("Hennings Privata chat");
        dummy.add("Johans och Claudes chat");
        return dummy;
    }
    private static void dummy_connect(User user, String chat_name) {
        System.out.println(user.toString());
        System.out.println(chat_name);
    }
    private static void dummy_disconnect(User user, String chat_name) {
        System.out.println(user.getName());
        System.out.println(chat_name);
    }
    private static void dummy_send_message(Message message, String chat_name) {
        System.out.println(message.toString());
        System.out.println(chat_name);
    }
}
