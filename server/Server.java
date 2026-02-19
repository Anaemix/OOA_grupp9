package server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * This class creates and starts the Database connection, Http server and Websocket server.
 * 
 * @author Henning
 * @version 0.1
 */
public class Server {

    /** Constructor added to prevent javadoc warning */
    public Server() {}

    /**
     * This class creates and starts the Database connection, Http server and Websocket server.
     * @param args Input arguments
     * @throws IOException Exceptions that could be thrown
     */
    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws IOException {
        int http_port = 2345;
        int websocket_port = 2346;
        DatabaseHandler db = new DatabaseHandler();
        HttpServer server = HttpServer.create(new InetSocketAddress(http_port), 0); 
        server.createContext("/get_chat", new GetChatHandler(db));
        server.createContext("/get_chats", new GetChatsHandler(db));
        server.createContext("/connect", new ConnectHandler(db));
        server.createContext("/disconnect", new DisconnectHandler(db));
        server.createContext("/send_message", new SendMessageHandler(db));
        server.setExecutor(null);
        server.start();
        System.out.println(String.format("Http Server up at port: %d", http_port));
        new WebsocketHandler(websocket_port, db).start();
        System.out.println(String.format("Websocket Server Started at port: %d", websocket_port));
    }

}
