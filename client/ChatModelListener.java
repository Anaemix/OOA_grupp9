package client;

import java.util.ArrayList;

/**
 * ChatModelListener - Observer interface for ChatModel changes.
 * Allows the View to be notified of model updates without the Model knowing about Swing.
 */
public interface ChatModelListener {
    
    /**
     * Called when a single message is added to the model.
     */
    void onMessageAdded(Chat chat);

    void onChatsLoaded(ArrayList<String> chats);

    void onChatSelected(Chat chat);

    void onMessageReceived(Message message);

    //void notifyMessageAdded(Chat chat);

    void updateUserList(String chatName, String[] activeUsers, ArrayList<String> inChatUsers);
}
