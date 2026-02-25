package client;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import server.Gson_InstantTypeAdapter;

/**
 * ChatController - The Controller component of the MVC pattern.
 * Bridges the Model and View, handling user interactions and updating the model.
 */
public class ChatController {
    private final ChatModel model;
    private final ChatView view;
    private ClientWebSocketHandler webSocket;

    public ChatController(ChatModel model, ChatView view) {
        this.model = model;
        this.view = view;
        initializeWebSocket();
    }

    public void initializeWebSocket() {
        try {
            URI uri = new URI("ws://fjenhh.me:2346");
            webSocket = new ClientWebSocketHandler(uri);
            webSocket.addListener(new WebSocketEventListener() {
                @Override
                public void onMessageReceived(Message message) {

                    handleIncomingMessage(message);
                }
                
                @Override
                public void onConnected() {
                    System.out.println("▪ws   ◀─▶ Connection established with address: " + webSocket.getLocalSocketAddress());
                }
                
                @Override
                public void onDisconnected() {
                    System.out.println("WebSocket disconnected");
                }
                
                @Override
                public void onError(String error) {
                    System.out.println("WebSocket error: " + error);
                }
            });
            webSocket.connectBlocking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Initializes the controller by setting up the view and attaching event listeners.
     */
    public void initialize(String inituser) {
        // Register the View as a listener to the Model (Observer pattern)
        model.addListener(view);
        String jsonMessage = "{\"t\":\"connect\", \"user\":\"" + inituser + "\"}";
        webSocket.sendMessageToServer(jsonMessage);
        System.err.println("▪ws   ──▶ type: connect, Body: " + jsonMessage);
        
        // Create and show the UI on the Event Dispatch Thread
        EventQueue.invokeLater(() -> {
            view.createAndShowUi(inituser);
            attachEventListeners();

        // Mock-chattar för test (anropas EFTER UI är skapad). Låg i ChatView tidigare
        //ArrayList<String> chats = new ArrayList<>();
        //chats.add("chat1");
        //chats.add("chat2");
        //chats.add("chat3");
        //chats.add("chat4");
        // chats.add(new Chat("chat4")); //Hur det var när vi hade chatobjekt innan vi bytte till strängar.
        //model.setChats(chats);
        });
    }

    /**
     * Attaches event listeners to the view components.
     */
    private void attachEventListeners() {
        view.addSendButtonListener(evt -> handleSendMessage());
        view.addInputFieldListener(evt -> handleSendMessage());
        view.addLoadButtonListener(evt -> handleLoadMockMessages());
        view.addClearButtonListener(evt -> handleClearMessages());
        view.addSendImageButtonListener(evt -> handleSendImageMessage());
        view.addAddChatButtonListener(evt -> handleAddChat());
        view.addLoginButtonListener(evt -> handleLogin());
        view.addChatSelectionListener(evt -> handleChatSelection(new Chat(evt.getActionCommand())));
        view.addDisconnectButtonListener(evt -> handleDisconnect());
    }

    private void handleAddChat() {
    String chatName = view.getAddChatText();

    if (chatName != null && !chatName.trim().isEmpty()) {
        model.addChat(chatName);
        view.clearAddChatField();
        }
    }


    private void handleIncomingMessage(Message message) {
        view.onMessageReceived(message);
    // Parse JSON and update model
    //Message msg = gson.fromJson(message, Message.class);
    //model.addMessage(msg, model.getCurrentChat());
    }
    public void sendMessage(Message message) {
    //    String json = gson.toJson(message);
    //    webSocket.sendMessageToServer(json);
    }


    private void handleChatSelection(Chat chatName) {
        Chat currentChat = ConnectionHandler.Get_Chat(chatName.getChatName());
        String jsonMessage = "{\"t\":\"enterchat\", \"chat\":\"" + chatName.getChatName() + "\"}";
        webSocket.sendMessageToServer(jsonMessage);
        System.out.println("▪ws   ──▶ type: enterchat, Body: " + jsonMessage);
        model.setCurrentChat(currentChat);
    }


    private void handleLogin() {
        String username = view.getLoginText();
        if (username != null && !username.trim().isEmpty()) {
            String jsonMessage = "{\"t\":\"connect\", \"user\":\"" + username + "\"}";
            webSocket.sendMessageToServer(jsonMessage);
            System.err.println("▪ws   ──▶ type: connect, Body: " + jsonMessage);
            model.setUser(new User(username));
            try {Files.writeString(Path.of(".user"), username, StandardCharsets.UTF_8);}
            catch (IOException e) {e.printStackTrace();}
            
        }
    }

    private void handleDisconnect() {
        ConnectionHandler.Disconnect(model.getUser(), model.getCurrentChat().getChatName());
        model.setChats(ConnectionHandler.Get_Chats(model.getUser()));
        model.setCurrentChat(null);
    }

    /**
     * Handles the send message action.
     */
    private void handleSendMessage() {
        String text = view.getInputText();
        Message message = model.createMessage(text);
        Chat chat = model.getCurrentChat();
        if (text != null && !text.trim().isEmpty()) {
            Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter()).create();
            String jsonMessage = "{\"t\":\"send\", \"chat\":\"" + chat.getChatName() + "\", \"message\":" + gson.toJson(message, Message.class) + "}";
            webSocket.sendMessageToServer(jsonMessage);
            System.out.println("▪ws   ──▶ type: send, Body: " + jsonMessage);
            //model.addMessage(message, chat);
            view.clearInputField();
        }
    }

        /**
     * Handles the send message action.
     */
    private void handleSendImageMessage() {
        JFileChooser file = new JFileChooser();
        file.setFileFilter(new FileNameExtensionFilter("Images (PNG, JPG, GIF)", "png", "jpg", "jpeg", "gif"));

        int result = file.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
           File selectedFile = file.getSelectedFile();
           
           long maxSize = 5*1024*1024;
           if (selectedFile.length() > maxSize) {
                JOptionPane.showMessageDialog(null, "Image must be smaller than 5 MB", "File too large", JOptionPane.ERROR_MESSAGE);
                return;
           }

           try {
                String hash = ConnectionHandler.Send_Image(selectedFile.getAbsolutePath());
                //byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                //String base64Image = Base64.getEncoder().encodeToString(fileBytes);

                Message message = model.createImageMessage(hash);
                Chat chat = model.getCurrentChat();

                Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new Gson_InstantTypeAdapter())
                    .create();
                    String jsonMessage = "{\"t\":\"send\", \"chat\":\"" + chat.getChatName() + "\", \"message\":" + gson.toJson(message, Message.class) + "}";
                    webSocket.sendMessageToServer(jsonMessage);
                    System.out.println("▪ws   ──▶ type: send (image), Body: " + jsonMessage.length());
           } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Failed to read image file.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
           }
        }
    }

    /**
     * Handles loading mock messages (simulating server data).
     */
    private void handleLoadMockMessages() {
        // Pretend these arrived from the server
        List<String> mockMessages = Arrays.asList(
            "Hello there",
            "This shows an array of strings",
            "Use it as your chat history"
        );
        model.loadMessages(mockMessages);
    }

    /**
     * Handles clearing all messages.
     */
    private void handleClearMessages() {
        model.clearMessages();
    }

    /**
     * Main entry point for the application.
     */
    public static void main(String[] args) {
        String inituser = "DefaultUser";
        try {
            if (Files.exists(Path.of(".user")))
                inituser = Files.readString(Path.of(".user"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChatModel model = new ChatModel();
        ChatView view = new ChatView();
        ChatController controller = new ChatController(model, view);
        controller.initialize(inituser);
        model.setUser(new User(inituser));
    }
}