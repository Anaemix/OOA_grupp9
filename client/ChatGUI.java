package client;

import java.awt.*;
import javax.swing.*;

public class ChatGUI {
    private JPanel mainPanel;

    public ChatGUI() {
        mainPanel = new JPanel(new BorderLayout());
    }
    
    public ChatGUI(Chat chat) {
        BorderLayout layout = new BorderLayout();
        mainPanel = new JPanel(layout);

        JScrollPane messagePanel = new JScrollPane(new Box(BoxLayout.Y_AXIS));
        JPanel userPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(userPanel, BoxLayout.Y_AXIS);
        userPanel.setLayout(boxLayout);

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
