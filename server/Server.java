import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

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
            
            String chat_name = httpexchange.getRequestURI().getPath().replace("/get_chat/", "");
            
            String response = String.format("{\"chat\": \"%s\", \"messages\": \"[datadatadatadata...]\"}", chat_name); 
            httpexchange.getResponseHeaders().add("Content-Type", "application/json");
            httpexchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = httpexchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}