package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Handles the graphical user interface for a chat session.
 * This class manages the display of chat messages (text and images)
 * and the list of users in a split-pane view.
 */
public class ChatGUI {
    private final JPanel mainPanel;
    private JPanel messagePanel;
    private JPanel userPanel;
    private JScrollPane chatScroll;
    private JScrollPane userScroll;

    /**
     * Constructs a default ChatGUI with an empty border layout.
     */
    public ChatGUI() {
        mainPanel = new JPanel(new BorderLayout());
    }
    
    /**
     * Constructs a ChatGUI populated with data from an existing Chat object.
     * Initializes layouts, populates message history, and sets up the user list.
     * * @param chat The Chat object containing message history and user data.
     */
    public ChatGUI(Chat chat) {
        BorderLayout layout = new BorderLayout();
        mainPanel = new JPanel(layout);

        this.messagePanel = new JPanel();
        BoxLayout messageLayout = new BoxLayout(messagePanel, BoxLayout.Y_AXIS);
        messagePanel.setLayout(messageLayout);

        userPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(userPanel, BoxLayout.Y_AXIS);
        userPanel.setLayout(boxLayout);
        userPanel.setBorder(BorderFactory.createTitledBorder("Users"));

        for(Message message : chat.getMessages()) {
            ArrayList<JLabel> messageLabel;

            messageLabel = createMessagePanel(message);

            for (JLabel m : messageLabel) {
                messagePanel.add(m);
            }
            messagePanel.revalidate();
            messagePanel.repaint();
            SwingUtilities.invokeLater(() -> {
                chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum());
            });
            }

        chatScroll = new JScrollPane(messagePanel);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        userScroll = new JScrollPane(userPanel);
        userScroll.setPreferredSize(new Dimension(120, 0));
        userScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        userScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(chatScroll, BorderLayout.CENTER);
        mainPanel.add(userScroll, BorderLayout.EAST);
        }
    
    /**
     * Returns the main container of the GUI.
     * @return The JPanel containing the entire chat interface.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Updates the user list panel with the current active users
     * @param activeUsers A list of usernames that are currently active/online.
     * @param inChatUsers A list of usernames that are currently in the chat room.
     */
    public void buildUserListPanel(ArrayList<String> activeUsers, ArrayList<String> inChatUsers) {
        userPanel.removeAll();
        for(String user : inChatUsers) {
            if (activeUsers.contains(user)) {
                JLabel userLabel = new JLabel(user.toString() + " (online)");
                userPanel.add(userLabel);
            } else {
                JLabel userLabel = new JLabel(user.toString());
                userPanel.add(userLabel);
            }
        }
        userPanel.revalidate();
        userPanel.repaint();
    }

    /**
     * Transforms a Message object into a list of JLabels for display.
     * Handles timestamp formatting, user identification, and image rendering.
     * @param message The message data to be converted.
     * @return An ArrayList of JLabels (header, content, and spacer).
     */
    public ArrayList<JLabel> createMessagePanel(Message message) {
        DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("MMM dd HH:mm")
        .withZone(ZoneId.of("GMT+1"));

        JLabel msgLabel;
        JLabel messageSpacer = new JLabel(" ");

        JLabel userTimeLabel = new JLabel(" ( " + message.getUser().getName() + " │ " + formatter.format(message.getTime()) + " )");

        if (message.isImage()) {
            msgLabel = new JLabel();
            try {
                byte[] imageBytes = Files.readAllBytes(Path.of("resources", message.getText()));
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

                if (img != null) {
                    int maxDim = 300;
                    int width = img.getWidth();
                    int height = img.getHeight();

                    if (width > maxDim || height > maxDim) {
                        double scaling = Math.min((double) maxDim / width, (double) maxDim / height);
                        width = (int) (width * scaling);
                        height = (int) (height * scaling);
                    }
                    Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

                    msgLabel = new JLabel(new ImageIcon(scaledImage));
                    msgLabel.setToolTipText("Image from " + message.getUser().getName() + " | " + formatter.format(message.getTime()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {
            msgLabel = new JLabel(" " + message.getText());
        }

        ArrayList<JLabel> list = new ArrayList<>();
        list.add(userTimeLabel);
        list.add(msgLabel);
        list.add(messageSpacer);

        return list;
    }
    
    /**
     * Adds a new message to the display in real-time.
     * This method updates the UI components and scrolls to the bottom automatically.
     * @param message The Message object to be added to the panel.
     */
    public void addMessage(Message message) {
        ArrayList<JLabel> messageLabel;
        
        messageLabel = createMessagePanel(message);
        for (JLabel m : messageLabel) {
            messagePanel.add(m);
        }
        messagePanel.revalidate();
        messagePanel.repaint();
        
        SwingUtilities.invokeLater(() -> {
            chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum());
        });
    }
}