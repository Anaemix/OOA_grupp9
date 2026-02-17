package client;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * ChatController - The Controller component of the MVC pattern.
 * Bridges the Model and View, handling user interactions and updating the model.
 */
public class ChatController {
    private final ChatModel model;
    private final ChatView view;
    private User loggedInUser;

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
        view.addWindowCloseListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleCloseWindow();
            }
        });
        view.addLoginButtonListener(e -> handleLogin());
        view.addDisconnectButtonListener(e -> handleDisconnect());
        view.setChatSelectedCallback(this::handleChatSelected);
    }


    private void handleChatSelected(String chatName) {
        model.setCurrentChat(chatName);
        ConnectionHandler.Connect(loggedInUser, chatName);
        
    }

    private void handleLogin() {
        String username = view.getLoginFieldText();
        if(username != null && !username.trim().isEmpty()) {
            loggedInUser = new User(0, username);
            model.setCurrentUser(loggedInUser);
            ArrayList<String> chats = ConnectionHandler.Get_Chats(loggedInUser);
            model.setChats(chats);
        } 
    }


    private void handleDisconnect() {
            ConnectionHandler.Disconnect(loggedInUser, model.getCurrentChat());
            loggedInUser = null;
            model.clearMessages();
            model.setChats(new ArrayList<>());
    }



    private void handleAddChat() {
    String chatName = view.getAddChatText();
    if (chatName != null && !chatName.trim().isEmpty()) {
        model.addChat(chatName);
        view.clearAddChatField();
    }
}

    /**
     * Handles the send message action.
     */
    private void handleSendMessage() {
        String text = view.getInputText();
        if (text != null && !text.trim().isEmpty()
            && loggedInUser != null && model.getCurrentChat() != null) {
            model.addMessage(text);
            view.clearInputField();
            ConnectionHandler.Send_Message(new Message(text, Instant.now(), loggedInUser), model.getCurrentChat());
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


    private void handleCloseWindow() { 
        int option = JOptionPane.showConfirmDialog(view.getFrame(), "Are you sure you want to exit", "Exit", 
            JOptionPane.YES_NO_OPTION);
            if(option == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
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
