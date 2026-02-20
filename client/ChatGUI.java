package client;

import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
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

            DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("MMM dd HH:mm")
            .withZone(ZoneId.of("GMT+1"));

            JLabel messageLabel = new JLabel(" " + message.toString() + "   ( " + message.getUser().getName() + " │ " + formatter.format(message.getTime()) + " )");
            System.out.println(message.toString());
            //JButton chatButton = new JButton(message.toString());
            //System.out.println(chatButton.getText());


            //gridBag.setConstraints(messageLabel, c);
            messagePanel.add(messageLabel);

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

    public void Update(Chat chat) {
        for(Message message : chat.getMessages()) {
            JLabel messageLabel = new JLabel(message.toString());
            messagePanel.add(messageLabel);
            System.out.println(message.toString());
            JButton chatButton = new JButton("hejhej");
            System.out.println(chatButton.getText());
            messagePanel.add(chatButton);
        }

        Set<String> addedUsers = new HashSet<>(); // maybe for the database
        for(User user : chat.getUsers()) {
            if(addedUsers.add(user.getName())) {
                JLabel userLabel = new JLabel(user.toString());
                userPanel.add(userLabel);
            }
        
        }
    }
}
