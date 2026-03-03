package client;

import java.util.ArrayList;

/**
 * ChatModelListener - Observer interface for ChatModel changes.
 * Allows the View to be notified of model updates without the Model knowing about Swing.
 */
public interface ChatModelListener {
    
    /**
     * Triggered when the local user adds a message to a chat.
     * @param chat The Chat object in which the message is being added.
     */
    void onMessageAdded(Chat chat);

    /**
     * Triggered when the list of available chat rooms has been fetched from the server.
     * Use this to rebuild the sidebar or chat navigation list.
     * @param chats An ArrayList of strings representing the names of available chat rooms.
     */
    void onChatsLoaded(ArrayList<String> chats);

    /**
     * Triggered when a specific chat room has been selected by the user.
     * This notifies the view to switch the display to the messages of the chosen chat.
     * @param chat The Chat object representing the newly selected room.
     */
    void onChatSelected(Chat chat);

    /**
     * Triggered when a new message is received from the server.
     * This allows the UI to append a single message to the current view without
     * refreshing the whole chat.
     * @param message The incoming Message object.
     */
    void onMessageReceived(Message message);

    /**
     * Triggered when there is an update to the users present in a specific chat.
     * This updates both the globally active users and those specifically in the room.
     * @param chatName    The name of the chat room being updated.
     * @param activeUsers A list of all users currently online on the server.
     * @param inChatUsers A list of users specifically joined to the named chat room.
     */
    void updateUserList(String chatName, String[] activeUsers, ArrayList<String> inChatUsers);
}
