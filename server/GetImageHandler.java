package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;


/**
 * This http handler class handles the joining/connecting to a chat room.
 */
public class GetImageHandler implements HttpHandler {

    /** Constructor */
    public GetImageHandler(DatabaseHandler databaseHandler) {}

    /**
     * This handles the http get_image request. 
     * Will respond with statuscodes <br>
     * -200 OK <br>
     * -400 Bad Request, if an exception was raised while accessing the file<br>
     * -405 Method Not Allowed, if POST request method was not used <br>
     * Test with "curl http://fjenhh.me:2345/get_image/{imagename}"
     * @param httpexchange http exchange to be handled by the function
     */
    public void handle(HttpExchange httpexchange) throws IOException {
        if (httpexchange.getRequestMethod().equalsIgnoreCase("GET")) {
            try {
                String image_name = httpexchange.getRequestURI().getPath().replace("/get_image/", "");
                Path image_path = Paths.get("resources", image_name);
                if (!Files.exists(image_path)) {
                    httpexchange.sendResponseHeaders(404, -1);
                    return;
                }
                byte[] imageBytes = Files.readAllBytes(image_path);
                String response = Base64.getEncoder().encodeToString(imageBytes);

                httpexchange.getResponseHeaders().add("Content-Type", "application/json");
                httpexchange.sendResponseHeaders(200, response.getBytes().length);
                
                try (OutputStream os = httpexchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                httpexchange.sendResponseHeaders(400, -1);
            }
        } else {
            httpexchange.sendResponseHeaders(405, -1);
        }
    }
}
