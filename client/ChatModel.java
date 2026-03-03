package client;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatModel - The Model component of the MVC pattern.
 * This class is responsible for the application's business logic and state management.
 * Communicates with the server via the ConnectionHandler.
 * As an Observable, it notifies registered ChatModelListeners
 * whenever the state changes (e.g., new messages or loaded chats).
 */
public class ChatModel {
    /** List of all listeners */
    private final List<ChatModelListener> listeners;
    /** The current chat the client is viewing */
    private Chat currentChat;
    /** The currently logged in user */
    private User user;
    /** ConnectionHandler */
    private ConnectionHandler connectionHandler;

    /**
     * Constructs a new ChatModel.
     * Initializes message and listener lists and sets up the server connection parameters.
     */
    public ChatModel() {
        this.listeners = new ArrayList<>();
        this.connectionHandler = new ConnectionHandler("FJENHH.me", "2345");
    }

    /**
     * Sets the current user and fetches the available chat rooms for that user.
     * @param user The User to log in.
     */
    public void setUser(User user) {
        this.user = user;
        setChats(ConnectionHandler.Get_Chats(user));
    }

    /**
     * Updates the local list of chat rooms and notifies all listeners.
     * @param chats An ArrayList of chat room names.
     */
    public void setChats(ArrayList<String> chats) {
        notifyChatsLoaded(chats);
    }

    /**
     * Fetches the latest list of chat rooms from the server for the current user.
     * @return The updated list of chat room names.
     */
    public ArrayList<String> getChats() {
        ArrayList<String> chats = ConnectionHandler.Get_Chats(user);
        return chats;
    }

    /**
     * Sets the currently active chat room and notifies listeners to update the view.
     * @param currentChat The Chat room to be displayed.
     */
    public void setCurrentChat(Chat currentChat) {
        this.currentChat = currentChat;
        notifyChatSelected(currentChat);
    }

    /** @return The currently selected Chat room. */
    public Chat getCurrentChat() {
        return currentChat;
    }

    /** @return The User currently using the application. */
    public User getUser() {
        return user;
    }

    /**
     * Creates a new chat room and links the current user to it.
     * If a chat room with the specified name already exists, join instead.
     * Automatically refreshes the chat list upon success.
     * @param chat The name of the chat room to add/join.
     */
    public void addChat(String chat) {
        if (chat != null && user != null && user.getName() != null) {
            ConnectionHandler.Connect(user, chat);
            getChats();
            ArrayList<String> chats = new ArrayList<>();
            chats.add(chat);
            notifyChatsLoaded(chats);
        }
    }

    /**
     * Registers a listener to receive model change notifications.
     * @param listener The ChatModelListener to add.
     */
    public void addListener(ChatModelListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener from receiving notifications.
     * @param listener The ChatModelListener to remove.
     */
    public void removeListener(ChatModelListener listener) {
        listeners.remove(listener);
    }

    /**
     * Sends a message to a specific chat room.
     * Updates the local message list and triggers a listener notification with
     * the updated chat history from the server.
     * @param message The Message object to send.
     * @param chat The Chat destination for the message.
     */
    public void addMessage(Message message, Chat chat) {
        if (message != null && !message.toString().trim().isEmpty()) {
            System.out.println("sending message" + message);
            ConnectionHandler.Send_Message(message, chat.getChatName());
            Chat updatedChat = ConnectionHandler.Get_Chat(chat.getChatName());
            notifyMessageAdded(updatedChat);
        }
    }

    /**
     * Method to create a standard text message with the current timestamp and user.
     * @param m The text content of the message.
     * @return A new Message object configured for text.
     */
    public Message createMessage(String m) {
        Instant timestamp = Instant.now();
        User currentUser = this.user;
        return new Message(m, timestamp, currentUser, false);
    }

    /**
     * Method to create an image-based message.
     * @param hash The unique identifier or path for the image file.
     * @return A new Message object configured as an image.
     */
    public Message createImageMessage(String hash) {
        Instant timestamp = Instant.now();
        User currentUser = this.user;
        return new Message(hash, timestamp, currentUser, true);
    }

    // --- Observer notification methods ---

    /** Notifies listeners that the list of available chats has been updated. */
    private void notifyChatsLoaded(ArrayList<String> chats) {
        for (ChatModelListener listener : listeners) {
            listener.onChatsLoaded(chats);
        }
    }

    /** Notifies listeners that a different chat room has been selected. */
    private void notifyChatSelected(Chat chat) {
        for (ChatModelListener listener : listeners) {
            listener.onChatSelected(chat);
        }
    }

    /** Notifies listeners that a new message has been added to a chat. */
    private void notifyMessageAdded(Chat chat) {
        for (ChatModelListener listener : listeners) {
            listener.onMessageAdded(chat);
        }
    }
}
