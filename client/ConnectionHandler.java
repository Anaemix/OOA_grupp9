package client;
import server.Gson_InstantTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.time.Instant;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.security.MessageDigest;
import java.util.HexFormat;

public class ConnectionHandler {
	/** Ip address of the http api */
	private static String ip_address;
	/** Port of the http api */
	private static String port;
	/** Gson instance for serialization & deserialization */
	private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
	
	/**
	 * Constructor for ConnectionHandler. Initializes the connection handler with the specified IP address and port for server communication.
	 * @param ip_address The IP address of the server to which the connection handler will communicate for HTTP requests.
	 * @param port The port number on which the server is listening for HTTP requests. This, combined with the IP address, will be used to construct the base URL for all server interactions.
	 */
	public ConnectionHandler(String ip_address, String port) {
		ConnectionHandler.ip_address = ip_address;
		ConnectionHandler.port = port;
	}

	/**
	 * Retrieves a list of chat names that the specified user is a member of. Makes a GET request to the server with the user's name and parses the response to return an ArrayList of chat names. If the server response is not successful, it returns an empty ArrayList.
	 * @param user The User object representing the user for whom the chat list is to be retrieved. The user's name is used in the GET request to the server to identify which chats to return.
	 * @return An ArrayList of strings, where each string is the name of a chat that the specified user is a member of. If the server response is not successful (i.e., status code is not 200), it returns an empty ArrayList.
	 */
	public static ArrayList<String> Get_Chats(User user) {	
		String url = String.format("%s/%s", Create_url("get_chats"), user.getName());
		
		ArrayList<String> output = new ArrayList<>();
		ServerResponse response = MakeGetRequest(url);

		if(response.getStatusCode() == 200) {
			Type listArrayType = new TypeToken<ArrayList<String>>() {}.getType();
			output = gson.fromJson(response.getBody(), listArrayType);
		}
		
		return output;
	}

	/**
	 * Retrieves a Chat object from the server based on the provided chat name. Makes a GET request to the server and parses the response to create a Chat object. Additionally, it verifies that all images in the messages of the chat are downloaded locally by checking if they exist in the "resources" directory and downloading them if they do not exist.
	 * @param chat The name of the chat to be retrieved from the server.
	 * @return A Chat object containing the chat information, messages, and users. If the server response is not successful, it returns a Chat object with only the chat name and empty messages and users.
	 */
	public static Chat Get_Chat(String chat) {
		String url = String.format("%s/%s", Create_url("get_chat"), chat.replace(" ", "%20"));
		
		Chat output = new Chat(chat);
		ServerResponse response = MakeGetRequest(url);
		
		if(response.getStatusCode() == 200) {
			output = gson.fromJson(response.getBody(), Chat.class);
		}
		
		//Verify that all images in messages are downloaded
		for(Message message : output.getMessages()) {
			if(message.isImage()) {
				Get_Image(message.getText());
			}
		}

		return output;
	}

	/**
	 * Handles the connection of a user to a chat. Constructs a JSON payload containing the user and chat information, and makes a POST request to the server to notify it of the new connection. The server is expected to add the user to the specified chat and update its records accordingly.
	 * @param user	 The user to be connected to the chat.
	 * @param chat The name of the chat to which the user is to be connected.
	 */
	public static void Connect(User user, String chat) {
		String url = String.format("%s", Create_url("connect"));

		JsonObject payload = new JsonObject();
		payload.add("user", gson.toJsonTree(user));
		payload.addProperty("chat", chat);

		MakePostRequest(url, gson.toJson(payload));
	}

	/**
	 * Handles the disconnection of a user from a chat. Constructs a JSON payload containing the user and chat information, and makes a POST request to the server to notify it of the disconnection. The server is expected to remove the user from the specified chat and update its records accordingly.
	 * @param user The user to be disconnected from the chat.
	 * @param chat The name of the chat from which the user is to be disconnected.
	 */
	public static void Disconnect(User user, String chat) {
				String url = String.format("%s", Create_url("disconnect"));

		JsonObject payload = new JsonObject();
		payload.add("user", gson.toJsonTree(user));
		payload.addProperty("chat", chat);

		MakePostRequest(url, gson.toJson(payload));
	}

	/**
	 * Sends a message to the server within a specific chat. Constructs a JSON payload containing the message and chat information, and makes a POST request to the server to send the message. The server is expected to handle the message and distribute it to other users in the chat.
	 * @param message The message object to be sent to the server.
	 * @param chat The name of the chat in which the message is to be sent.
	 */
	public static void Send_Message(Message message, String chat) {
				String url = String.format("%s", Create_url("send_message"));

		JsonObject payload = new JsonObject();
		payload.add("message", gson.toJsonTree(message));
		payload.addProperty("chat", chat);

		MakePostRequest(url, gson.toJson(payload));
	}	

	/**
	 * Retrieves an image from the server based on the provided hash. If the image already exists locally in the "resources" directory, it does nothing. Otherwise, it makes a GET request to the server to retrieve the image data, decodes it from Base64, and saves it to the "resources" directory with the filename as the hash.
	 * @param hash The hash string representing the image to be retrieved from the server. This hash is used to check if the image already exists locally and to request the image from the server if it does not exist.
	 */
	public static void Get_Image(String hash) {
		if (Files.exists(Path.of("resources", hash))) {
			return;
		}
		String url = String.format("%s/%s", Create_url("get_image"), hash);
		
		ServerResponse response = MakeGetRequest(url);
		
		try {
			byte[] imageBytes = Base64.getDecoder().decode(response.getBody());
			Files.write(Paths.get("resources", hash), imageBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Sends an image to the server by reading the image file, encoding it in Base64, and making a POST request with the encoded image as the payload. The server is expected to save the image and return a hash that can be used to retrieve the image later.
	 * @param image_path The file path of the image to be sent to the server.
	 * @return The hash string returned by the server that can be used to retrieve the image, or null if an error occurs during the process.
	 */
	public static String Send_Image(String image_path) {
		try {

			byte[] imageBytes = Files.readAllBytes(Paths.get(image_path));
			String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(imageBytes));
			String url = String.format("%s/%s", Create_url("post_image"), hash);

			JsonObject payload = new JsonObject();


			String imageString = Base64.getEncoder().encodeToString(imageBytes);
			payload.addProperty("image", imageString);

			MakePostRequest(url, gson.toJson(payload));
			return hash;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Creates a URL string based on the provided path, using the configured IP address and port.
	 * @param path The specific path to be appended to the base URL (e.g., "get_chats", "send_message").
	 * @return A complete URL string in the format "http://{ip_address}:{port}/{path}" ready for making HTTP requests to the server.
	 */
	private static String Create_url(String path) {
		return String.format("http://%s:%s/%s", ip_address, port, path);
	}

	/**
	 * Makes a GET request to the specified URL and returns the server response.
	 * @param url The URL to which the GET request is sent.
	 * @return A ServerResponse object containing the response body and status code from the server.
	 */
	private static ServerResponse MakeGetRequest(String url) {
		ServerResponse response = new ServerResponse("Request Failed",0);
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest
			.newBuilder()
			.uri(URI.create(url))
			.GET()
			.build();
		System.out.println(String.format("┌http ──▶ Requesting: %s", url));
		try {
			HttpResponse<String> httpresponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			response.setBody(httpresponse.body());
			response.setStatusCode(httpresponse.statusCode());
			System.out.println(String.format("└http ◀── Statuscode: %d, Body: %s", response.getStatusCode(), response.getBody().length() <= 150 ? response.getBody() : response.getBody().substring(0,149)));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return response;
	}

	/**
	 * Makes a POST request to the specified URL with the given body and returns the server response.
	 * @param url The URL to which the POST request is sent.
	 * @param body The body of the POST request, typically in JSON format.
	 * @return A ServerResponse object containing the response body and status code from the server.
	 */
	private static ServerResponse MakePostRequest(String url, String body) {
		ServerResponse response = new ServerResponse("Request Failed",0);
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest
			.newBuilder()
			.uri(URI.create(url))
			.header("Content-Type", "application/json")
			.POST(BodyPublishers.ofString(body))
			.build();
		System.out.println(String.format("┌http ──▶ Requesting: %s, Body: %s", url, body));
		try {
			HttpResponse<String> httpresponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			response.setBody(httpresponse.body());
			response.setStatusCode(httpresponse.statusCode());
			System.out.println(String.format("└http ◀── %d, Body: %s", response.getStatusCode(), response.getBody().length() <= 150 ? response.getBody() : response.getBody().substring(0,149)));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	/** 
	 * ServerResponse - A simple class to encapsulate the response from the server, including the response body and status code. This class is used to standardize the way responses are handled in the ConnectionHandler when making HTTP requests.
	 */
	private static class ServerResponse {
		/** The body of the server response */
		private String body;
		/** The HTTP status code of the server response */
		private int statuscode;
		/** Constructor */
		public ServerResponse(String body, int statuscode) {
			this.body = body;
			this.statuscode = statuscode;
		}
		/** Returns the body of the server response */
		public String getBody() {
			return this.body;
		}
		/** Returns the HTTP status code of the server response */
		public int getStatusCode() {
			return this.statuscode;
		}
		/** Sets the body of the server response */
		public void setBody(String body) {
			this.body = body;
		}
		/** Sets the HTTP status code of the server response */
		public void setStatusCode(int statuscode) {
			this.statuscode = statuscode;
		}
	}
}