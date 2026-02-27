package client;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Provides a graphical interface for displaying a list of available chat rooms.
 * This class uses a GridBagLayout to stack chat selection buttons
 * vertically at the top of the panel.
 */
public class ChatListGUI {
    private final JPanel chatListPanel;
    
    /**
     * Constructs an empty ChatListGUI with a GridBagLayout.
     */
    public ChatListGUI() {
        this.chatListPanel = new JPanel(new GridBagLayout());
    }

    /**
     * Constructs a ChatListGUI populated with buttons for each chat room.
     * Buttons are configured to stretch horizontally and anchor to the top,
     * with a spacer at the bottom to prevent buttons from spreading out vertically.
     * * @param chats An ArrayList of strings representing the names
     * of the available chat rooms.
     */
    public ChatListGUI(ArrayList<String> chats) {
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.NORTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.chatListPanel = new JPanel(gridBag);

        for (String chat : chats) {
            JButton chatButton = new JButton(chat);
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

    /**
     * Returns the panel containing the chat list components.
     * * @return The JPanel displaying the chat buttons.
     */
    public JPanel getChatListPanel() {
        return chatListPanel;
    }

}
