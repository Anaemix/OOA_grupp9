package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

        this.messagePanel = new JPanel();
        BoxLayout messageLayout = new BoxLayout(messagePanel, BoxLayout.Y_AXIS);
        messagePanel.setLayout(messageLayout);


        mainPanel = new JPanel(layout);

        userPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(userPanel, BoxLayout.Y_AXIS);
        userPanel.setLayout(boxLayout);
        userPanel.setPreferredSize(new Dimension(100, 0));

        //messagePanel.setBorder(BorderFactory.createTitledBorder("Messages"));
        userPanel.setBorder(BorderFactory.createTitledBorder("Users"));

        for(Message message : chat.getMessages()) {
            JPanel messageLabel;
            
            //if(message.isImage() != null && message.isImage()) {
            //    messageLabel = createImageLabel(message);
            //} else {
            //    messageLabel = new JLabel(formatMessage(message));
            //}

            messageLabel = createMessagePanel(message);
            messagePanel.add(messageLabel);
            }
            //System.out.println(message.toString());
            //JButton chatButton = new JButton(message.toString());
            //System.out.println(chatButton.getText());


            //gridBag.setConstraints(messageLabel, c);


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

        messagePanel.add(spacer, spacerGbc);
        userPanel.add(spacer,spacerGbc);


        chatScroll = new JScrollPane(messagePanel);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        userScroll = new JScrollPane(userPanel);
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

        JPanel messagePanel = new JPanel(new BorderLayout());
        JLabel messageLabel = new JLabel();
        JLabel messageSpacer = new JLabel(" ");

        JLabel userTimeLabel = new JLabel(" ( " + message.getUser().getName() + " │ " + formatter.format(message.getTime()) + " )");

        if (message.isImage()) {
            messageLabel = new JLabel();
            try {
                byte[] imageBytes = Base64.getDecoder().decode(Files.readString(Path.of("resources", message.getText())));
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

                    messageLabel = new JLabel(new ImageIcon(scaledImage));
                    messageLabel.setToolTipText("Image from " + message.getUser().getName() + " | " + formatter.format(message.getTime()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {
            messageLabel = new JLabel(" " + message.getText());
        }

        
        messagePanel.add(userTimeLabel, BorderLayout.NORTH);
        messagePanel.add(messageLabel, BorderLayout.WEST);
        messagePanel.add(messageSpacer, BorderLayout.SOUTH);
        return messagePanel;
    }
    

    public void addMessage(Message message) {
        JLabel messageLabel;
        
        if (message.isImage() != null && message.isImage()) {
            messageLabel = createImageLabel(message);
        } else {
            messageLabel = new JLabel(formatMessage(message));
        }
        
        messagePanel.add(messageLabel);
        messagePanel.revalidate();
        messagePanel.repaint();
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
