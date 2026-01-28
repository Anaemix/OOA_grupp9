package client;

import java.util.ArrayList;

public class Chat {
    private final ArrayList<Message> messages;
    private final ArrayList<User> users;
    private final String chatName;

    public Chat(String chatName) {
        this.chatName = chatName;
        this.messages = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    public String getChatName() {
        return chatName;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public String toString() {
        return chatName;
    }
}
