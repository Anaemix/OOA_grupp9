package client;
import server.Gson_InstantTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.time.Instant;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.security.MessageDigest;
import java.util.HexFormat;

/*     public static ArrayList<String> Get_Chats(User user) {    
-> returnar lista av alla chatter som User är med i

public static Chat Get_Chat(String chat) {
-> Returnerar en chat baserat på dess namn (med messages och users etc)

    public static void Connect(User user, String chat) {
-> lägger till en user i en chatt (eller skapar chatten/usern om den inte redan finns)

    public static void Disconnect(User user, String chat) {
-> tar bort user från en chat

    public static void Send_Message(Message message, String chat) {
->selfexplanatory */

public class ConnectionHandler {
	private static String ip_address = "localhost";
	private static String port = "2345";
	private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
	
	public ConnectionHandler(String ip_address, String port) {
		this.ip_address = ip_address;
		this.port = port;
	}
//   public static void main(String[] args) {
		//System.out.println(Get_Chats(new User("Henning")).toString());

		//Chat chat = Get_Chat("Hennings Privata chat");
		//System.out.println(String.format("\nChatname: %s\nMessages: %s\nUsers: %s", chat.toString(), chat.getMessages().toString(), chat.getUsers().toString()));
		
		//Connect(new User("Henning"), "Hennings Hemliga Chat");

		//Disconnect(new User("Henning"), "Hennings Hemliga Chat");

		//Message message = new Message("My new message", Instant.now(), new User("Henning"));
		//Send_Message(message, "My new chat");
//	}

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

	public static Chat Get_Chat(String chat) {
		String url = String.format("%s/%s", Create_url("get_chat"), chat.replace(" ", "%20"));
		
		Chat output = new Chat(chat);
		ServerResponse response = MakeGetRequest(url);
		
		if(response.getStatusCode() == 200) {
			output = gson.fromJson(response.getBody(), Chat.class);
		}
		
		return output;
	}

	public static void Connect(User user, String chat) {
		String url = String.format("%s", Create_url("connect"));

		JsonObject payload = new JsonObject();
		payload.add("user", gson.toJsonTree(user));
		payload.addProperty("chat", chat);

		MakePostRequest(url, gson.toJson(payload));
	}

	public static void Disconnect(User user, String chat) {
				String url = String.format("%s", Create_url("disconnect"));

		JsonObject payload = new JsonObject();
		payload.add("user", gson.toJsonTree(user));
		payload.addProperty("chat", chat);

		MakePostRequest(url, gson.toJson(payload));
	}

	public static void Send_Message(Message message, String chat) {
				String url = String.format("%s", Create_url("send_message"));

		JsonObject payload = new JsonObject();
		payload.add("message", gson.toJsonTree(message));
		payload.addProperty("chat", chat);

		MakePostRequest(url, gson.toJson(payload));
	}	

	public static boolean Get_Image(String hash) {
		String url = String.format("%s/%s", Create_url("get_image"), hash);
		
		ServerResponse response = MakeGetRequest(url);

		try {
			byte[] imageBytes = Base64.getDecoder().decode(response.getBody());
			Files.write(Paths.get("resources", hash + ".png"), imageBytes);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

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
	private static String Create_url(String path) {
		return String.format("http://%s:%s/%s", ip_address, port, path);
	}

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
			System.out.println(String.format("└http ◀── %d, Body: %s", response.getStatusCode(), response.getBody()));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return response;
	}

	private static class ServerResponse {
		private String body;
		private int statuscode;
		
		public ServerResponse(String body, int statuscode) {
			this.body = body;
			this.statuscode = statuscode;
		}
		public String getBody() {
			return this.body;
		}
		public int getStatusCode() {
			return this.statuscode;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public void setStatusCode(int statuscode) {
			this.statuscode = statuscode;
		}
	}
}