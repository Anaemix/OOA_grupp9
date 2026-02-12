package client;

import java.awt.*;
import javax.swing.*;

public class ChatGUI {
    private JPanel mainPanel;

    public ChatGUI() {
        // Default constructor for empty chat view
    }
    
    public ChatGUI(Chat chat) {
        BorderLayout layout = new BorderLayout();
        mainPanel = new JPanel(layout);

        ScrollPane messagePanel = new ScrollPane();
        JPanel userPanel = new JPanel();

        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(userPanel, BorderLayout.EAST);

        for(Message message : chat.getMessages()) {
            JLabel messageLabel = new JLabel(message.toString());
            messagePanel.add(messageLabel);
        }

        for(User user : chat.getUsers()) {
            JLabel userLabel = new JLabel(user.toString());
            userPanel.add(userLabel);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
