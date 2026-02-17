package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
import javax.swing.SwingUtilities;

/**
 * ChatView - The View component of the MVC pattern.
 * Responsible for displaying the UI. Contains no business logic.
 * Implements ChatModelListener(Observer) to receive updates from the Model.
 */
public class ChatView implements ChatModelListener {
    private JFrame frame;
    private JList<String> messageList;
    private JTextField inputField;
    private JButton sendButton;
    private JButton sendImageButton;
    private JButton loadButton;
    private JButton clearButton;
    private JButton addChatButton;
    private JButton loginButton;
    private JButton disconnectButton;
    private JTextField loginField;
    private JTextField addChatField;
    private DefaultListModel<String> displayModel;
    private ChatListGUI chatListGUI;
    private JPanel chatListPanel;
    private JScrollPane chatListScrollPane;
    private JPanel leftPanel;
    private Consumer<String> chatSelectedCallback;

    /**
     * Creates and displays the UI.
     */
    public void createAndShowUi() {
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    
        
        

        // Internal display model for Swing (View's own data)
        displayModel = new DefaultListModel<>();

        // Chat message list
        messageList = new JList<>(displayModel);
        JScrollPane scrollPane = new JScrollPane(messageList);
        scrollPane.setPreferredSize(new Dimension(360, 240));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Messages"));

        // Input area
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(600, 28));

        // Buttons
        sendButton = new JButton("Send");
        sendImageButton = new JButton("Send Image");
        loadButton = new JButton("Load mock array");
        clearButton = new JButton("Clear");

        // Message control panel for constructing and sending a message/image or clearing messages
        JPanel messageControler = new JPanel(new BorderLayout());
        messageControler.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(sendButton);
        buttonPanel.add(sendImageButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(clearButton);

        messageControler.add(inputField, BorderLayout.CENTER);
        messageControler.add(buttonPanel, BorderLayout.EAST);

        
        
        addChatField = new JTextField();
        addChatField.setPreferredSize(new Dimension(100, 28));
        addChatButton = new JButton("Add Chat");
        JPanel addChat = new JPanel(new GridLayout(1,2));
        addChat.add(addChatField);
        addChat.add(addChatButton);

        loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(100, 28));
        loginButton = new JButton("Login");
        disconnectButton = new JButton ("Disconnect");
        JPanel login = new JPanel(new GridLayout(2, 2));
        login.add(loginField);
        login.add(loginButton);
        login.add(new JLabel("")); //Utfyllnadslabel för att få disconnect-knappen på rätt plats
        login.add(disconnectButton);
        
        
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Chats"));
        
        leftPanel.add(addChat, BorderLayout.NORTH);
        leftPanel.add(login, BorderLayout.SOUTH);
        
        // Flyttade testchattar till ChatController för MVC-struktur
        
        JPanel chatUsers = new JPanel();
        chatUsers.setLayout(new BoxLayout(chatUsers, BoxLayout.Y_AXIS));
        chatUsers.setBorder(BorderFactory.createTitledBorder("Users"));
        chatUsers.setPreferredSize(new Dimension(100, 240));
        JLabel label = new JLabel("Hugo");
        JLabel label2 = new JLabel("Henning");
        
        chatUsers.add(label);
        chatUsers.add(label2);
        
        JScrollPane chatUserScrollPane = new JScrollPane(chatUsers);
        chatUserScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatUserScrollPane.getVerticalScrollBar().setValue(chatUserScrollPane.getVerticalScrollBar().getMaximum());
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(chatUserScrollPane, BorderLayout.EAST);
        rightPanel.add(messageControler, BorderLayout.SOUTH);


        // Assemble frame
        
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.add(leftPanel, BorderLayout.WEST);
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

    @Override
    public void onChatsLoaded(ArrayList<String> chats) {
        // Ta bort gammal chattlista om den finns
        if (chatListScrollPane != null) {
            leftPanel.remove(chatListScrollPane);
        }
        
        // Skapa ny chattlista
        chatListGUI = new ChatListGUI(chats, callback -> {
            if (chatSelectedCallback != null) {
                chatSelectedCallback.accept(callback);
            }
        });
        chatListPanel = chatListGUI.getChatListPanel();
        chatListScrollPane = new JScrollPane(chatListPanel);
        chatListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        SwingUtilities.invokeLater(() -> { chatListScrollPane.getVerticalScrollBar().setValue(chatListScrollPane.getVerticalScrollBar().getMaximum()); });
        leftPanel.add(chatListScrollPane, BorderLayout.CENTER);
        
        // Uppdatera UI
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    @Override
    public void onChatSelected(String chat) {

    }

    // --- Getters and listener registration ---

    public void clearAddChatField() { addChatField.setText("");}

    public String getAddChatText() { return addChatField.getText();}

    public void addAddChatButtonListener(ActionListener listener) {
        addChatButton.addActionListener(listener);
    }

    /**
     * Returns the text from the input field.
     */
    public String getInputText() {
        return inputField.getText();
    }

    /**
     * Clears the input field.
     */
    public void clearInputField() {
        inputField.setText("");
    }

    public String getLoginFieldText() {
        return loginField.getText();
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

    public void addWindowCloseListener(WindowListener listener) {
        frame.addWindowListener(listener);
    }


    public void addLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addDisconnectButtonListener(ActionListener listener) {
        disconnectButton.addActionListener(listener);
    }
    /**
     * Returns the main frame.
     */
    public JFrame getFrame() {
        return frame;
    }

    public void setChatSelectedCallback(Consumer<String> callback) {
        this.chatSelectedCallback = callback;
    }


    public void setChatSelectedCallback(Object callback) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setChatSelectedCallback'");
    }
}

