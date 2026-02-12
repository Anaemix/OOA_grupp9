package client;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatModel - The Model component of the MVC pattern.
 * Responsible for storing and managing chat message data.
 * UI-agnostic: uses Observer pattern to notify listeners of changes.
 */
public class ChatModel {
    private final List<String> messages;
    private final List<ChatModelListener> listeners;
    private ArrayList<String> chats = new ArrayList<>();
    private String currentChat;
    private ConnectionHandler connectionHandler;
    private User user;

    public ChatModel() {
        this.messages = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.connectionHandler = new ConnectionHandler("localhost", "2345");
        setUser(new User(0, "DefaultUser")); // Initialize with a default user or provide a method to set the user
    }

    public void setUser(User user) {
        this.user = user;
        setChats(ConnectionHandler.Get_Chats(user));
    }

    public void setChats(ArrayList<String> chats) {
        this.chats = chats;
        notifyChatsLoaded(chats);
    }

    public ArrayList<String> getChats() {
        chats = ConnectionHandler.Get_Chats(user);
        return chats;
    }

    public void setCurrentChat(String currentChat) {
        this.currentChat = currentChat;
        notifyChatSelected(currentChat);
    }

    public String getCurrentChat() {
        return currentChat;
    }

    public void addChat(String chat) {
        if (chat != null && user != null && user.getName() != null) {
            System.out.println("Adding chat: " + chat);
            chats.add(chat);
            ConnectionHandler.Connect(user, chat);
            getChats();
            notifyChatsLoaded(chats);
        }
    }

    /**
     * Registers a listener to receive model change notifications.
     */
    public void addListener(ChatModelListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener from receiving notifications.
     */
    public void removeListener(ChatModelListener listener) {
        listeners.remove(listener);
    }

    /**
     * Adds a single message to the model.
     */
    public void addMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            String trimmed = message.trim();
            messages.add(trimmed);
            notifyMessageAdded(trimmed);
        }
    }

    /**
     * Clears all messages from the model.
     */
    public void clearMessages() {
        messages.clear();
        notifyMessagesCleared();
    }

    /**
     * Loads a list of messages into the model, replacing any existing messages.
     */
    public void loadMessages(List<String> newMessages) {
        messages.clear();
        if (newMessages != null) {
            messages.addAll(newMessages);
        }
        notifyMessagesLoaded(new ArrayList<>(messages));
    }

    /**
     * Returns the number of messages in the model.
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * Returns a copy of all messages as a list.
     */
    public List<String> getAllMessages() {
        return new ArrayList<>(messages);
    }


    // --- Observer notification methods ---

    private void notifyChatsLoaded(ArrayList<String> chats) {
        for (ChatModelListener listener : listeners) {
            listener.onChatsLoaded(chats);
        }
    }

    private void notifyChatSelected(String chat) {
        for (ChatModelListener listener : listeners) {
            listener.onChatSelected(chat);
        }
    }

    private void notifyMessageAdded(String message) {
        for (ChatModelListener listener : listeners) {
            listener.onMessageAdded(message);
        }
    }

    private void notifyMessagesCleared() {
        for (ChatModelListener listener : listeners) {
            listener.onMessagesCleared();
        }
    }

    private void notifyMessagesLoaded(List<String> messages) {
        for (ChatModelListener listener : listeners) {
            listener.onMessagesLoaded(messages);
        }
    }
}
