package client;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;

/**
 * ChatController - The Controller component of the MVC pattern.
 * Bridges the Model and View, handling user interactions and updating the model.
 */
public class ChatController {
    private final ChatModel model;
    private final ChatView view;

    public ChatController(ChatModel model, ChatView view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Initializes the controller by setting up the view and attaching event listeners.
     */
    public void initialize() {
        // Register the View as a listener to the Model (Observer pattern)
        model.addListener(view);
        
        // Create and show the UI on the Event Dispatch Thread
        EventQueue.invokeLater(() -> {
            view.createAndShowUi();
            attachEventListeners();

        // Mock-chattar för test (anropas EFTER UI är skapad). Låg i ChatView tidigare
        ArrayList<String> chats = new ArrayList<>();
        chats.add("chat1");
        chats.add("chat2");
        chats.add("chat3");
        chats.add("chat4");
        // chats.add(new Chat("chat4")); //Hur det var när vi hade chatobjekt innan vi bytte till strängar.
        model.setChats(chats);
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
    }

    private void handleAddChat() {
    String chatName = view.getAddChatText();

    if (chatName != null && !chatName.trim().isEmpty()) {
        model.addChat(chatName);
        view.clearAddChatField();
        }
    }

    private void handleChatSelection(Chat chatName) {
        Chat currentChat = ConnectionHandler.Get_Chat(chatName.getChatName());
        System.out.println("Chat selected: " + currentChat.getChatName());
        model.setCurrentChat(currentChat);
    }


    private void handleLogin() {
        String username = view.getLoginText();
        if (username != null && !username.trim().isEmpty()) {
            model.setUser(new User(username));
        }
    }

    /**
     * Handles the send message action.
     */
    private void handleSendMessage() {
        String text = view.getInputText();
        Message message = model.createMessage(text);
        Chat chat = model.getCurrentChat();
        if (text != null && !text.trim().isEmpty()) {
            model.addMessage(message, chat);
            view.clearInputField();
        }
    }

        /**
     * Handles the send message action.
     */
    private void handleSendImageMessage() {
        JFileChooser j = new JFileChooser(".");
        j.showSaveDialog(null);
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
        ChatModel model = new ChatModel();
        ChatView view = new ChatView();
        ChatController controller = new ChatController(model, view);
        controller.initialize();
    }
}