package client;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * ChatController - The Controller component of the MVC pattern.
 * Bridges the Model and View, handling user interactions and updating the model.
 */
public class ChatController {
    /** The model in the MVC */
    private final ChatModel model;
    /** The view in the MVC */
    private final ChatView view;
    /** Websocket client */
    private ClientWebSocketHandler webSocket;
    private Semaphore chatListSemaphore = new Semaphore(1);

    /**
     * Constructor for ChatController. Initializes the model, view, and WebSocket connection.
     * @param model The ChatModel instance representing the application's data and business logic.
     * @param view The ChatView instance responsible for the user interface and presentation logic.
     */
    public ChatController(ChatModel model, ChatView view) {
        this.model = model;
        this.view = view;
        initializeWebSocket();
    }

    /**
     * Initializes the WebSocket connection to the server and sets up event listeners for WebSocket events.
     */
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

                @Override
                public void updateUserList(String chatName, String[] activeUsers, ArrayList<String> inChatUsers) {
                    chatListSemaphore.acquireUninterruptibly();
                    view.updateUserList(chatName, activeUsers, inChatUsers);
                    chatListSemaphore.release();
                }
            });
            webSocket.connectBlocking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Initializes the controller by setting up the view and attaching event listeners.
     * @param inituser The initial username to log in with
     */
    public void initialize(String inituser) {
        // Register the View as a listener to the Model (Observer pattern)
        model.addListener(view);
        webSocket.login(inituser);
        
        // Create and show the UI on the Event Dispatch Thread
        EventQueue.invokeLater(() -> {
            view.createAndShowUi(inituser);
            attachEventListeners();
        });
    }

    /**
     * Attaches event listeners to the view components.
     */
    private void attachEventListeners() {
        view.addSendButtonListener(evt -> handleSendMessage());
        view.addSendImageButtonListener(evt -> handleSendImageMessage());
        view.addAddChatButtonListener(evt -> handleAddChat());
        view.addLoginButtonListener(evt -> handleLogin());
        view.addChatSelectionListener(evt -> handleChatSelection(new Chat(evt.getActionCommand())));
        view.addDisconnectButtonListener(evt -> handleDisconnect());
    }

    /**
     * Handles the action of adding a new chat room. Retrieves the chat name from the view, updates the model, and clears the input field.
     */
    private void handleAddChat() {
        String chatName = view.getAddChatText();
        chatListSemaphore.acquireUninterruptibly();

        if (chatName != null && !chatName.trim().isEmpty()) {
            model.addChat(chatName);
            view.clearAddChatField();
        }
        chatListSemaphore.release();
    }


    /**
     * Takes an incoming message and updates the view accordingly.
     * @param message The Message object representing the incoming message from the server.
     */
    private void handleIncomingMessage(Message message) {
        chatListSemaphore.acquireUninterruptibly();
        view.onMessageReceived(message);
        chatListSemaphore.release();
    }

    /**
     * Handles the selection of a chat room.
     * @param chat The Chat object representing the selected chat room.
     */
    private void handleChatSelection(Chat chat) {
        chatListSemaphore.acquireUninterruptibly();
        Chat currentChat = ConnectionHandler.Get_Chat(chat.getChatName());
        webSocket.enterChat(chat.getChatName());
        model.setCurrentChat(currentChat);
        chatListSemaphore.release();
    }

    /**
     * Logs in the user, updates the model with a new user and sends the login to the server
     */
    private void handleLogin() {
        String username = view.getLoginText();
        if (username != null && !username.trim().isEmpty()) {
            webSocket.login(username); // Send login message to server
            model.setUser(new User(username));

            // Save the username to a file for future sessions
            try {Files.writeString(Path.of(".user"), username, StandardCharsets.UTF_8);}
            catch (IOException e) {e.printStackTrace();}
            
        }
    }
    /**
     * Handles the disconnect action. Notifies the server of the disconnection and reloads the chat list.
     */
    private void handleDisconnect() {
        chatListSemaphore.acquireUninterruptibly();
        ConnectionHandler.Disconnect(model.getUser(), model.getCurrentChat().getChatName());
        model.setChats(ConnectionHandler.Get_Chats(model.getUser()));
        model.setCurrentChat(null);
        chatListSemaphore.release();
    }

    /**
     * Handles the send message action. Clears the input field and supplies the message to the WebSocket handler to send to the server.
     */
    private void handleSendMessage() {
        String text = view.getInputText();
        Message message = model.createMessage(text);
        Chat chat = model.getCurrentChat();
        if (text != null && !text.trim().isEmpty()) {
            webSocket.sendMessageToServer(message, chat.getChatName());
            view.clearInputField();
        }
    }

    /**
     * Handles the send image message action. 
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
                Message message = model.createImageMessage(hash);
                Chat chat = model.getCurrentChat();
                webSocket.sendMessageToServer(message, chat.getChatName());
           } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Failed to read image file.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
           }
        }
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
        try {
            if (!Files.exists(Path.of("resources")))
                Files.createDirectory(Path.of("resources"));
        } catch (IOException e) {e.printStackTrace();}

        ChatModel model = new ChatModel();
        ChatView view = new ChatView();
        ChatController controller = new ChatController(model, view);
        controller.initialize(inituser);
        model.setUser(new User(inituser));
    }
}