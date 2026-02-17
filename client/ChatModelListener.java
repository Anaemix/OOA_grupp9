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
    void onMessageAdded(String message);
    
    /**
     * Called when all messages are cleared from the model.
     */
    void onMessagesCleared();
    
    /**
     * Called when messages are bulk-loaded into the model.
     * @param messages The list of messages that were loaded.
     */
    void onMessagesLoaded(List<String> messages);

    /**
     * Called when the list of chats is loaded or updated in the model.
     * @param chats The updated list of chat names.
     */
    void onChatsLoaded(ArrayList<String> chats);

    /**
     * Called when a chat is selected in the UI.
     * @param chat The name of the chat that was selected.
     */
    void onChatSelected(String chat);
}
