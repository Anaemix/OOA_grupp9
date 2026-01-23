package client;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * ChatModel - The Model component of the MVC pattern.
 * Responsible for storing and managing chat message data.
 */
public class ChatModel {
    private final DefaultListModel<String> messagesModel;

    public ChatModel() {
        this.messagesModel = new DefaultListModel<>();
    }

    /**
     * Returns the underlying list model for binding to the view.
     */
    public DefaultListModel<String> getMessagesModel() {
        return messagesModel;
    }

    /**
     * Adds a single message to the model.
     */
    public void addMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            messagesModel.addElement(message.trim());
        }
    }

    /**
     * Clears all messages from the model.
     */
    public void clearMessages() {
        messagesModel.clear();
    }

    /**
     * Loads a list of messages into the model, replacing any existing messages.
     */
    public void loadMessages(List<String> messages) {
        messagesModel.clear();
        if (messages != null) {
            messages.forEach(messagesModel::addElement);
        }
    }

    /**
     * Returns the number of messages in the model.
     */
    public int getMessageCount() {
        return messagesModel.size();
    }

    /**
     * Returns a copy of all messages as a list.
     */
    public List<String> getAllMessages() {
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < messagesModel.size(); i++) {
            messages.add(messagesModel.get(i));
        }
        return messages;
    }
}
