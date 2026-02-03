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
            view.LoginUi();
            //view.createAndShowUi();
            attachEventListeners();
        });
    }

    /**
     * Attaches event listeners to the view components.
     */
    private void attachEventListeners() {
        //view.addSendButtonListener(evt -> handleSendMessage());
        //view.addInputFieldListener(evt -> handleSendMessage());
        //view.addLoadButtonListener(evt -> handleLoadMockMessages());
        //view.addClearButtonListener(evt -> handleClearMessages());
        //view.addSendImageButtonListener(evt -> handleSendImageMessage());
        view.addLoginButtonListener(evt -> handleLogin());
        view.addLoginFieldListener(evt -> handleLogin());
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

    private void handleLogin() {
    System.out.println("Login handler called, login text = " + view.getLoginText());
    String text = view.getLoginText();

    Chat chat1 = new Chat("chat1");
    Chat chat2 = new Chat("chat2");
    Chat chat3 = new Chat("chat3");

    ArrayList<Chat> chats = new ArrayList<>();
    chats.add(chat1);
    chats.add(chat2);
    chats.add(chat3);

    User user1 = new User("1", "user1");

    chat1.addUser(user1);
    chat3.addUser(user1);

    ArrayList<Chat> viewChats = new ArrayList<>();
    for (Chat chat : chats) {
        for (User u : chat.getUsers()) {
            if (u.getName().equals(text)) {
                viewChats.add(chat);
                break;
            }
        }
    }

    ChatFrame chatFrame = new ChatFrame();
    chatFrame.createAndShowUi(viewChats);
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
