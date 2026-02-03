package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * ChatView - The View component of the MVC pattern.
 * Responsible for displaying the UI. Contains no business logic.
 * Implements ChatModelListener to receive updates from the Model.
 */
public class ChatFrame implements ChatModelListener {
    private JFrame frame;
    private JList<String> messageList;
    private JTextField inputField;
    private JButton sendButton;
    private JButton sendImageButton;
    private JButton loadButton;
    private JButton clearButton;
    private JButton addChatButton;
    private JButton loginButton;
    private JTextField addChatField;
    private DefaultListModel<String> displayModel;
    private ChatListGUI chatListGUI;

    /**
     * Creates and displays the UI.
     */
    public void createAndShowUi(ArrayList<Chat> chats) {
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Internal display model for Swing (View's own data)
        displayModel = new DefaultListModel<>();

        // Chat message list
        messageList = new JList<>(displayModel);
        JScrollPane scrollPane = new JScrollPane(messageList);
        scrollPane.setPreferredSize(new Dimension(360, 240));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Messages"));

        // Input area
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(200, 28));

        // Buttons
        sendButton = new JButton("Send");
        sendImageButton = new JButton("Send Image");
        loadButton = new JButton("Load mock array");
        clearButton = new JButton("Clear");

        // Controls panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(inputField);
        controls.add(sendButton);
        controls.add(sendImageButton);
        controls.add(loadButton);
        controls.add(clearButton);

        addChatField = new JTextField();
        addChatField.setPreferredSize(new Dimension(100, 28));
        addChatButton = new JButton("Add Chat");
        JPanel addChat = new JPanel(new GridLayout(1,2));
        addChat.add(addChatField);
        addChat.add(addChatButton);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Chats"));

        panel.add(addChat, BorderLayout.NORTH);
        //panel.add(login, BorderLayout.SOUTH);

        chatListGUI = new ChatListGUI(chats);

        panel.add(chatListGUI.getChatListPanel(), BorderLayout.CENTER);

        JPanel chatUsers = new JPanel();
        chatUsers.setLayout(new BoxLayout(chatUsers, BoxLayout.Y_AXIS));
        chatUsers.setBorder(BorderFactory.createTitledBorder("Users"));
        chatUsers.setPreferredSize(new Dimension(100, 240));
        for (Chat c : chats) {
            for (User u : c.getUsers()) {
                JLabel label = new JLabel(u.getName());
                chatUsers.add(label);
            }
        }

        // Assemble frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);
        frame.add(panel, BorderLayout.WEST);
        frame.add(chatUsers, BorderLayout.EAST);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // --- ChatModelListener implementation (Observer pattern) ---

    @Override
    public void onMessageAdded(String message) {
        displayModel.addElement(message);
        // Auto-scroll to bottom
        messageList.ensureIndexIsVisible(displayModel.size() - 1);
    }

    @Override
    public void onMessagesCleared() {
        displayModel.clear();
    }

    @Override
    public void onMessagesLoaded(List<String> messages) {
        displayModel.clear();
        for (String message : messages) {
            displayModel.addElement(message);
        }
    }

    public ChatListGUI getChatListGUI() {
        return chatListGUI;
    }

    // --- Getters and listener registration ---

    /**
     * Returns the text from the input field.
     */
    public String getInputText() {
        return inputField.getText();
    }

    public String getLoginText() {
        return inputField.getText();
    }

    public String getAddChatText() {
        return addChatField.getText();
    }

    /**
     * Clears the input field.
     */
    public void clearInputField() {
        inputField.setText("");
    }

    /**
     * Adds an action listener to the send button.
     */
    public void addSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

        /**
     * Adds an action listener to the send button.
     */
    public void addSendImageButtonListener(ActionListener listener) {
        sendImageButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the load button.
     */
    public void addLoadButtonListener(ActionListener listener) {
        loadButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the clear button.
     */
    public void addClearButtonListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the input field (for Enter key).
     */
    public void addInputFieldListener(ActionListener listener) {
        inputField.addActionListener(listener);
    }

    public void addLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    
    public void addChatButtonListener(ActionListener listener) {
        addChatButton.addActionListener(listener);
    }

    /**
     * Returns the main frame.
     */
    public JFrame getFrame() {
        return frame;
    }
}

