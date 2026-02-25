package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
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
    Component glue = Box.createVerticalGlue();

    public ChatGUI() {
        mainPanel = new JPanel(new BorderLayout());
    }
    
    public ChatGUI(Chat chat) {
        BorderLayout layout = new BorderLayout();

        //GridBagLayout gridBag = new GridBagLayout();
        //GridBagConstraints c = new GridBagConstraints();
        //c.fill = GridBagConstraints.HORIZONTAL;
        //c.weightx = 1.0;
        //c.anchor = GridBagConstraints.NORTH;
        //c.gridwidth = GridBagConstraints.REMAINDER;

        //this.messagePanel = new JPanel(gridBag);

        this.messagePanel = new JPanel(new GridBagLayout());
        BoxLayout messageLayout = new BoxLayout(messagePanel, BoxLayout.Y_AXIS);
        messagePanel.setLayout(messageLayout);

        mainPanel = new JPanel(layout);

        userPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(userPanel, BoxLayout.Y_AXIS);
        userPanel.setLayout(boxLayout);

        //messagePanel.setBorder(BorderFactory.createTitledBorder("Messages"));
        userPanel.setBorder(BorderFactory.createTitledBorder("Users"));


        for(Message message : chat.getMessages()) {
            JPanel messageContainer;
            
            messageContainer = createMessagePanel(message);
            messagePanel.add(messageContainer);
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

        //messagePanel.add(spacer, spacerGbc);
        //messagePanel.add(Box.createVerticalGlue());
        //gbc.gridy = counter;
        //gbc.weighty = 1.0;
        //messagePanel.add(new JPanel(), gbc);

        //addDynamicPanel();

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


        //Message exMsg1 = new Message("hej", Instant.now(), new User("hugo1"));
        //chat.addMessage(exMsg1);
        //Update();
        }
    

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JPanel createMessagePanel(Message message) {
        DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("MMM dd HH:mm")
        .withZone(ZoneId.of("GMT+1"));
        JPanel msgPanel = new JPanel();
        JLabel msgLabel = new JLabel();
        JLabel messageSpacer = new JLabel(" ");

        JLabel userTimeLabel = new JLabel(" ( " + message.getUser().getName() 
            + " │ " + formatter.format(message.getTime()) + " )");

        if (message.isImage()) {
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

        msgPanel.add(userTimeLabel);
        msgPanel.add(msgLabel);
        msgPanel.add(messageSpacer);

        return msgPanel;
    }
    

    public void addMessage(Message message) {
        JPanel messageObject;
        
        messageObject = createMessagePanel(message);
        
        messagePanel.add(messageObject);

    
    messagePanel.revalidate();
    messagePanel.repaint();
    SwingUtilities.invokeLater(() -> {
        chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum());
    });
    
    }

    private JLabel createImageLabel(Message message) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(message.getText());
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

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd HH:mm")
                .withZone(ZoneId.of("GMT+1"));

            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setToolTipText("Image from " + message.getUser().getName() + " | "
                + formatter.format(message.getTime()));

                return imageLabel;
            }

        } catch (Exception e) {
                e.printStackTrace();
            }

            return new JLabel("Image could not be loaded");
    }


    private String formatMessage(Message message) {
        DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("MMM dd HH:mm")
            .withZone(ZoneId.of("GMT+1"));
        return " " + message.toString() + "   ( " + message.getUser().getName() + " │ " + formatter.format(message.getTime()) + " )";
    }
}
