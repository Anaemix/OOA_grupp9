package server;

import java.time.Instant;
import java.util.ArrayList;

import client.User;
import client.Message;
import client.Chat;

/**
 * Interface defining database operations for the chat server.
 */
public interface ChatDatabase {

    void closeConnection();

    void addUser(User user);

    void addChat(String chatname);

    void addUserToChat(User user, String chatname);

    void removeUserFromChat(User user, String chatName);

    void addMessage(String chatName, Message message);

    ArrayList<Message> getMessages(String chatname, Instant time);

    Chat getChat(String chatname);

    ArrayList<String> getAllChats(User user);
}