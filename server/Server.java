package server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) throws IOException {
        int port = 2345;
        DatabaseHandler db = new DatabaseHandler();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0); 
        server.createContext("/get_chat", new GetChatHandler(db));
        server.createContext("/get_chats", new GetChatsHandler(db));
        server.createContext("/connect", new ConnectHandler(db));
        server.createContext("/disconnect", new DisconnectHandler(db));
        server.createContext("/send_message", new SendMessageHandler(db));
        server.setExecutor(null);
        server.start();
        System.out.println(String.format("Server up at port :%d", port));
    }

}
