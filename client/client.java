import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class client {
    private final DefaultListModel<String> messagesModel = new DefaultListModel<>();

    public static void main(String[] args) {
        // Launch on the EDT to keep Swing happy.
        EventQueue.invokeLater(() -> new client().createAndShowUi());
    }

    private void createAndShowUi() {
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Chat message list.
        JList<String> messageList = new JList<>(messagesModel);
        JScrollPane scrollPane = new JScrollPane(messageList);
        scrollPane.setPreferredSize(new Dimension(360, 240));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Messages"));

        // Input area and buttons.
        JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(200, 28));

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(evt -> addMessageFromInput(inputField));

        JButton loadButton = new JButton("Load mock array");
        loadButton.addActionListener(this::loadMockMessages);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(evt -> messagesModel.clear());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(inputField);
        controls.add(sendButton);
        controls.add(loadButton);
        controls.add(clearButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addMessageFromInput(JTextField inputField) {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            messagesModel.addElement(text);
            inputField.setText("");
        }
    }

    private void loadMockMessages(ActionEvent evt) {
        // Pretend these arrived from the server.
        List<String> mock = Arrays.asList(
            "Hello there",
            "This shows an array of strings",
            "Use it as your chat history"
        );
        messagesModel.clear();
        mock.forEach(messagesModel::addElement);
    }
}