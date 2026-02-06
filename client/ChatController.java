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
        ArrayList<Chat> chats = new ArrayList<>();
        chats.add(new Chat("chat1"));
        chats.add(new Chat("chat2"));
        chats.add(new Chat("chat3"));
        chats.add(new Chat("chat4"));
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
    }

    private void handleAddChat() {
    String chatName = view.getAddChatText();
    if (chatName != null && !chatName.trim().isEmpty()) {
        model.addChat(new Chat(chatName));
        view.clearAddChatField();
    }
}

    /**
     * Handles the send message action.
     */
    private void handleSendMessage() {
        String text = view.getInputText();
        if (text != null && !text.trim().isEmpty()) {
            model.addMessage(text);
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