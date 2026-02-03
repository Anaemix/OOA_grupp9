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
import com.google.gson.GsonBuilder;
import java.lang.reflect.Array;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
            Gson gson =
                new GsonBuilder()
                .registerTypeAdapter( Instant.class , new Gson_InstantTypeAdapter() )
                .create();

            String chat_name = httpexchange.getRequestURI().getPath().replace("/get_chat/", "");
            String response = gson.toJson(dummy_get_chat(chat_name));
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

    private static Chat dummy_get_chat(String chat_name) {
        Chat dummy = new Chat(chat_name);
        User dummy_u1 = new User("1", "Coola Henning");
        User dummy_u2 = new User("2", "Najibis");
        User dummy_u3 = new User("3", "Mega Hugo");
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
    private static ArrayList<String> dummy_get_chats(String name) {
        ArrayList<String> dummy = new ArrayList<>();
        dummy.add("Hennings Privata chat");
        dummy.add("Johans och Claude's chat");
        return dummy;
    }
}