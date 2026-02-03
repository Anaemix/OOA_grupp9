package client;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import javax.swing.*;

public class ChatListGUI {
    private final JPanel chatListPanel;

    public ChatListGUI(ArrayList<Chat> chats) {
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.NORTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.chatListPanel = new JPanel(gridBag);

        for (Chat chat : chats) {
            JButton chatButton = new JButton(chat.getChatName());
            gridBag.setConstraints(chatButton, c);
            chatListPanel.add(chatButton);
        }
        JPanel spacer = new JPanel();
        GridBagConstraints spacerGbc = new GridBagConstraints();
        spacerGbc.gridx = 0;
        spacerGbc.gridy = GridBagConstraints.RELATIVE;
        spacerGbc.weighty = 1.0;
        spacerGbc.fill = GridBagConstraints.VERTICAL;
        spacerGbc.gridwidth = GridBagConstraints.REMAINDER;
        chatListPanel.add(spacer, spacerGbc);
    }

    public JPanel getChatListPanel() {
        return chatListPanel;
    }

    public void updateChatList(ArrayList<Chat> chats) {
        chatListPanel.removeAll();
        for (Chat chat : chats) {
            JButton chatButton = new JButton(chat.getChatName());
            gridBag.setConstraints(chatButton, c);
            chatListPanel.add(chatButton);
        }
        JPanel spacer = new JPanel();
        GridBagConstraints spacerGbc = new GridBagConstraints();
        spacerGbc.gridx = 0;
        spacerGbc.gridy = GridBagConstraints.RELATIVE;
        spacerGbc.weighty = 1.0;
        spacerGbc.fill = GridBagConstraints.VERTICAL;
        spacerGbc.gridwidth = GridBagConstraints.REMAINDER;
        chatListPanel.add(spacer, spacerGbc);
    }
}
