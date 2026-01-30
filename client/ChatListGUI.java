package client;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.*;

public class ChatListGUI {
    private final JPanel chatListPanel;

    public ChatListGUI(ArrayList<Chat> chats) {
        this.chatListPanel = new JPanel();
        chatListPanel.setLayout(new GridLayout(chats.size(), 1));
        for (Chat chat : chats) {
            JButton chatButton = new JButton(chat.getChatName());
            chatListPanel.add(chatButton);

        }
    }

    public JPanel getChatListPanel() {
        return chatListPanel;
    }

}
