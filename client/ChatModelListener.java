package client;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatModelListener - Observer interface for ChatModel changes.
 * Allows the View to be notified of model updates without the Model knowing about Swing.
 */
public interface ChatModelListener {
    
    /**
     * Called when a single message is added to the model.
     */
    void onMessageAdded(Chat chat);
    
    /**
     * Called when all messages are cleared from the model.
     */
    void onMessagesCleared();
    
    /**
     * Called when messages are bulk-loaded into the model.
     */
    void onMessagesLoaded(List<String> messages);

    void onChatsLoaded(ArrayList<String> chats);

    void onChatSelected(Chat chat);

    //void notifyMessageAdded(Chat chat);
}
