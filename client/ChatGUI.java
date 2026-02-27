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

public class ChatGUI {
    private final JPanel mainPanel;
    private JPanel messagePanel;
    private JPanel userPanel;
    private JScrollPane chatScroll;
    private JScrollPane userScroll;

    public ChatGUI() {
        mainPanel = new JPanel(new BorderLayout());
    }
    
    public ChatGUI(Chat chat) {
        BorderLayout layout = new BorderLayout();

        this.messagePanel = new JPanel(new GridBagLayout());
        BoxLayout messageLayout = new BoxLayout(messagePanel, BoxLayout.Y_AXIS);
        messagePanel.setLayout(messageLayout);

        mainPanel = new JPanel(layout);

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


        Set<String> addedUsers = new HashSet<>(); // maybe for the database
        for(User user : chat.getUsers()) {
            if(addedUsers.add(user.getName())) {
                JLabel userLabel = new JLabel(user.toString());
                userPanel.add(userLabel);
            }
        
        }

        JPanel spacer = new JPanel();
        GridBagConstraints spacerGbc = new GridBagConstraints();
        spacerGbc.gridx = 0;
        spacerGbc.gridy = GridBagConstraints.RELATIVE;
        spacerGbc.weighty = 1.0;
        spacerGbc.fill = GridBagConstraints.VERTICAL;
        spacerGbc.gridwidth = GridBagConstraints.REMAINDER;

        userPanel.add(spacer,spacerGbc);

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
    

    public JPanel getMainPanel() {
        return mainPanel;
    }

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