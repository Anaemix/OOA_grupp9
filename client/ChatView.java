package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * ChatView - The View component of the MVC pattern.
 * Responsible for displaying the UI. Contains no business logic.
 * Implements ChatModelListener(Observer) to receive updates from the Model.
 */
public class ChatView implements ChatModelListener {
    private JFrame frame;
    private JTextField inputField;
    private JButton sendButton;
    private JButton sendImageButton;
    private JButton addChatButton;
    private JTextField addChatField;
    private ChatListGUI chatListGUI;
    private JPanel chatListPanel;
    private JScrollPane chatListScrollPane;
    private JPanel leftPanel;
    private JButton loginButton;
    private JButton disconnectButton;
    private JTextField loginField;
    private JPanel chatPanel;
    private ChatGUI chatGUI;
    private ActionListener chatSelectionListener;
    private ArrayList<String> activeUsers = new ArrayList<>();

    /**
     * Initializes the GUI components, sets up layouts, and displays the main frame.
     * @param inituser The initial username to display in the login field.
     */
    public void createAndShowUi(String inituser) {
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Chat message list
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(400, 240));
        chatPanel.setBorder(BorderFactory.createTitledBorder("Messages"));

        // Controls panel for the chat

        // Message input control
        inputField = new JTextField();
        sendButton = new JButton("Send");
        sendImageButton = new JButton("Send Image");

        // Layout for controls panel
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
        controls.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controls.add(inputField);
        controls.add(Box.createRigidArea(new Dimension(5, 0)));
        controls.add(sendButton);
        controls.add(Box.createRigidArea(new Dimension(5, 0)));
        controls.add(sendImageButton);

        // AddChat panel
        addChatField = new JTextField();
        addChatField.setPreferredSize(new Dimension(100, 28));
        addChatButton = new JButton("Add Chat");
        JPanel addChat = new JPanel(new GridLayout(1,2));
        addChat.add(addChatField);
        addChat.add(addChatButton);

        // Login panel
        loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(100, 28));
        loginButton = new JButton("Login");
        disconnectButton = new JButton("Disconnect");
        JPanel login = new JPanel(new GridLayout(2, 2));
        loginField.setText(inituser);
        login.add(loginField);
        login.add(loginButton);
        login.add(new JLabel());
        login.add(disconnectButton);

        // Panel for AddChat and Login
        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Chats"));

        leftPanel.add(addChat, BorderLayout.NORTH);
        leftPanel.add(login, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(chatPanel, BorderLayout.CENTER);
        centerPanel.add(controls, BorderLayout.SOUTH);

        // Assemble frame
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(leftPanel, BorderLayout.WEST);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // --- ChatModelListener implementation (Observer pattern) ---

    /**
     * Updates the UI when a message is added to a specific chat.
     * Re-initializes the entire chat message panel.
     * @param chat The Chat object containing the updated message history.
     */
    @Override
    public void onMessageAdded(Chat chat) {
        chatGUI = new ChatGUI(chat);
        chatPanel.removeAll();
        chatPanel.add(chatGUI.getMainPanel(), BorderLayout.CENTER);
        ArrayList<String> users = new ArrayList<>();
        for (User user : chat.getUsers()) {users.add(user.getName());}
        chatGUI.buildUserListPanel(this.activeUsers, users);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    /**
     * Updates the current chat view with a single incoming message.
     * @param message The Message object to append to the view.
     */
    @Override
    public void onMessageReceived(Message message){
        chatGUI.addMessage(message);
    }

    /**
     * Updates the user list in the sidebar when a chat update is received.
     * @param chatName The name of the chat room being updated.
     * @param activeUsers An array of usernames currently active in the chat.
     * @param inChatUsers An ArrayList of usernames currently in the chat.
     */
    @Override
    public void updateUserList(String chatName, String[] activeUsers, ArrayList<String> inChatUsers) {
        this.activeUsers = new ArrayList<>();
        if (activeUsers != null) {
            for (String user : activeUsers) {
                this.activeUsers.add(user);
            }
        }

        chatGUI.buildUserListPanel(this.activeUsers, inChatUsers);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    /**
     * Populates the sidebar with the list of available chat rooms.
     * Ensures listeners are reapplied to new buttons.
     * @param chats An ArrayList of strings representing chat room names.
     */
    @Override
    public void onChatsLoaded(ArrayList<String> chats, String ActiveChat) {
        // Ta bort gammal chattlista om den finns
        if (chatListScrollPane != null) {
            leftPanel.remove(chatListScrollPane);
        }
        
        // Skapa ny chattlista
        chatListGUI = new ChatListGUI(chats, ActiveChat);
        chatListPanel = chatListGUI.getChatListPanel();
        chatListScrollPane = new JScrollPane(chatListPanel);
        chatListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftPanel.add(chatListScrollPane, BorderLayout.CENTER);

        applyChatSelectionListener(); // <-- Återapplicera listenern på de nya knapparna

        // Uppdatera UI
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    /**
     * Updates the display when a user selects a different chat room.
     * @param chat The Chat room data to display.
     */
    @Override
    public void onChatSelected(Chat chat) {
        chatGUI = new ChatGUI(chat);
        chatPanel.removeAll();
        chatPanel.add(chatGUI.getMainPanel(), BorderLayout.CENTER);
        ArrayList<String> users = new ArrayList<>();
        for (User user : chat.getUsers()) {users.add(user.getName());}
        chatGUI.buildUserListPanel(this.activeUsers, users);
        this.chatListGUI.setActiveChat(chat.getChatName());
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    // --- Getters and listener registration ---

    /** Clears the text in the "Add Chat" input field. */
    public void clearAddChatField() { addChatField.setText("");}

    /** @return The text currently in the "Add Chat" input field. */
    public String getAddChatText() { return addChatField.getText();}

    /**
     * Registers a listener for selecting chat rooms. 
     * The listener is stored to be reapplied when the chat list refreshes.
     * @param listener The ActionListener for chat room selection buttons.
     */
    public void addChatSelectionListener(ActionListener listener) {
        chatSelectionListener = listener;
        applyChatSelectionListener();
    }

    /** Registers a listener for the "Add Chat" button. */
    public void addAddChatButtonListener(ActionListener l) { addChatButton.addActionListener(l); }
    
    /** Registers a listener for the "Send" message button. */
    public void addSendButtonListener(ActionListener l) { sendButton.addActionListener(l); }
    
    /** Registers a listener for the "Send Image" button. */
    public void addSendImageButtonListener(ActionListener l) { sendImageButton.addActionListener(l); }
    
    /** Registers a listener for the "Login" button. */
    public void addLoginButtonListener(ActionListener l) { loginButton.addActionListener(l); }
    
    /** Registers a listener for the "Disconnect" button. */
    public void addDisconnectButtonListener(ActionListener l) { disconnectButton.addActionListener(l); }
    
    /** Registers a listener for the message input field (e.g., for Enter key). */
    public void addInputFieldListener(ActionListener l) { inputField.addActionListener(l); }

    /**
     * Internal helper to apply the stored chatSelectionListener to all
     * buttons in the current chat list panel.
     */
    private void applyChatSelectionListener() {
    if (chatSelectionListener == null || chatListGUI == null) return;
        for (Component chats : chatListGUI.getChatListPanel().getComponents()) {
            if (chats instanceof JButton button) {
                button.addActionListener(chatSelectionListener);
            }
        }
    }

    /** Removes content of chatPanel */
    public void removeChatPanel() {
        chatPanel.removeAll();
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    /** @return The text currently in the Login input field. */
    public String getLoginText() {
        return loginField.getText();
    }

    /** @return The text currently in the message input field. */
    public String getInputText() {
        return inputField.getText();
    }

    /** Clears the message input field. */
    public void clearInputField() {
        inputField.setText("");
    }

    /** @return The main JFrame of the application. */
    public JFrame getFrame() {
        return frame;
    }
}
