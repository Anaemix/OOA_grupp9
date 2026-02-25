package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * This http handler class handles the joining/connecting to a chat room.
 * 
 * @author Henning
 * @version 0.1
 */
public class PostImageHandler implements HttpHandler {
    /** The handler used for database persistence. */
    private final DatabaseHandler db;
    /** Gson object used for deserialization of json. */
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();

    /**
     * Constructor 
     * @param databaseHandler handles the database connection, writing/reading.
     */
    public PostImageHandler(DatabaseHandler databaseHandler) {
        this.db = databaseHandler;
    }
    /**
     * This handles the http request. 
     * Will respond with statuscodes <br>
     * -200 OK <br>
     * -400 Bad Request, if an exception was raised in the json parsing or addition of the user in the database <br>
     * -405 Method Not Allowed, if POST request method was not used <br>
     * Test with "curl http://fjenhh.me:2345/get_chat/{chatname}"
     * @param httpexchange http exchange to be handled by the function
     */
    public void handle(HttpExchange httpexchange) throws IOException {
        if (httpexchange.getRequestMethod().equalsIgnoreCase("POST")) {
            String response = "0";
            

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
