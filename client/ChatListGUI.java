package client;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import javax.swing.*;

public class ChatListGUI {
    private JPanel chatListPanel;
    private ArrayList<Chat> activeChats;

    public ChatListGUI(ArrayList<Chat> chats) {
        //this.chatListPanel = new JPanel();
        this.activeChats = new ArrayList<>();
        updateChatList(chats);

    }

    public void updateChatList(ArrayList<Chat> chats) {
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.NORTH;
        c.gridwidth = GridBagConstraints.REMAINDER;

        if (chatListPanel == null) {
            chatListPanel = new JPanel(gridBag);
        } else {
            //chatListPanel.removeAll();
            System.out.println("removed all components from chatListPanel");
            chatListPanel.setLayout(gridBag);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.anchor = GridBagConstraints.NORTH;
            c.gridwidth = GridBagConstraints.REMAINDER;
        }

        for (Chat chat : chats) {
            if (activeChats.contains(chat)) {
                continue;
            }
            JButton chatButton = new JButton(chat.getChatName());
            gridBag.setConstraints(chatButton, c);
            chatListPanel.add(chatButton);
            activeChats.add(chat);
        }

        JPanel spacer = new JPanel();
        GridBagConstraints spacerGbc = new GridBagConstraints();
        spacerGbc.gridx = 0;
        spacerGbc.gridy = GridBagConstraints.RELATIVE;
        spacerGbc.weighty = 1.0;
        spacerGbc.fill = GridBagConstraints.VERTICAL;
        spacerGbc.gridwidth = GridBagConstraints.REMAINDER;
        chatListPanel.add(spacer, spacerGbc);

        chatListPanel.revalidate();
        chatListPanel.repaint();
    }

    public void addChat(String chatName) {
        ArrayList<Chat> newChat = new ArrayList<>();
        newChat.add(new Chat(chatName));
        updateChatList(newChat);
    }

    public JPanel getChatListPanel() {
        return chatListPanel;
    }
}
