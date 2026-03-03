package client;

import java.util.ArrayList;

/**
 * This class acts as a data container for the chat's identity,
 * the history of messages sent, and the list of participating users.
 */
public class Chat {
    /** ArrayList of messages that have been sent in the chat */
    private final ArrayList<Message> messages;
    /** ArrayList of users that have joined the chat */
    private final ArrayList<User> users;
    /** Name of the chat */
    private final String chatName;

    /**
     * Constructs a new Chat instance with a specific name.
     * Initializes empty lists for both messages and users.
     * @param chatName The name to be assigned to this chat room.
     */
    public Chat(String chatName) {
        this.chatName = chatName;
        this.messages = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    /**
     * Gets the name of the chat room.
     * @return The chat name as a String.
     */
    public String getChatName() {
        return chatName;
    }

    /**
     * Gets the list of all messages recorded in this chat.
     * @return An ArrayList containing the Message objects.
     */
    public ArrayList<Message> getMessages() {
        return messages;
    }

    /**
     * Gets the list of users associated with this chat.
     * @return An ArrayList containing the User objects.
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Appends a new message to the chat history.
     * @param message The Message to be added.
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Adds a user to the list of participants in this chat.
     * @param user The User to be added.
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Returns a string representation of the chat, which is its name.
     * @return The name of the chat.
     */
    @Override
    public String toString() {
        return chatName;
    }
}
