package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * ChatView - The View component of the MVC pattern.
 * Responsible for displaying the UI. Contains no business logic.
 */
public class ChatView {
    private JFrame frame;
    private JList<String> messageList;
    private JTextField inputField;
    private JButton sendButton;
    private JButton loadButton;
    private JButton clearButton;

    /**
     * Creates and displays the UI with the given messages model.
     */
    public void createAndShowUi(DefaultListModel<String> messagesModel) {
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Chat message list
        messageList = new JList<>(messagesModel);
        JScrollPane scrollPane = new JScrollPane(messageList);
        scrollPane.setPreferredSize(new Dimension(360, 240));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Messages"));

        // Input area
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(200, 28));

        // Buttons
        sendButton = new JButton("Send");
        loadButton = new JButton("Load mock array");
        clearButton = new JButton("Clear");

        // Controls panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(inputField);
        controls.add(sendButton);
        controls.add(loadButton);
        controls.add(clearButton);

        // Assemble frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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

    /**
     * Adds an action listener to the send button.
     */
    public void addSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
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

    /**
     * Returns the main frame.
     */
    public JFrame getFrame() {
        return frame;
    }
}
