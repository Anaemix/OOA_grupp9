package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;


/**
 * This http handler class handles the joining/connecting to a chat room.
 */
public class PostImageHandler implements HttpHandler {
    /** Gson object used for deserialization of json. */
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();

    /** Constructor */
    public PostImageHandler() {}

    /**
     * This handles the http get request. 
     * Will respond with statuscodes <br>
     * -200 OK <br>
     * -400 Bad Request, if an exception was raised in the json parsing <br>
     * -405 Method Not Allowed, if POST request method was not used <br>
     * Test with "curl http://fjenhh.me:2345/get_chat/{chatname}"
     * @param httpexchange http exchange to be handled by the function
     */
    public void handle(HttpExchange httpexchange) throws IOException {
        if (httpexchange.getRequestMethod().equalsIgnoreCase("POST")) {

            try {
                String imagename = httpexchange.getRequestURI().getPath().replace("/post_image/", "");
                InputStream is = httpexchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String imageString = gson.fromJson(body, JsonObject.class).get("image").getAsString();
                System.out.println(String.format("Received image: %s", imageString));
                byte[] imageBytes = Base64.getDecoder().decode(imageString);
                
                Files.write(Paths.get("resources",imagename), imageBytes);
            } catch (Exception e) {
                e.printStackTrace();
                httpexchange.sendResponseHeaders(400, -1);
            }
            httpexchange.sendResponseHeaders(200, -1);
        
        } else {
            httpexchange.sendResponseHeaders(405, -1);
        }
    }
}
