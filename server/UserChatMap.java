package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Supporting bi directional data structure for storing (user, chat) tuples.
 * Users are unique while chats are not.
 * 
 * @author Henning
 * @version 0.1
 */
public class UserChatMap {
    /** Main hashmap storing the tuples */
    private final Map<String, String> userToChat = new HashMap<>();
    /** Supporting hashmap storing the chats with a set of users active in a chat */
    private final Map<String, Set<String>> chatToUser = new HashMap<>();

    /** Constructor added to prevent javadoc warning */
    public UserChatMap() {}

    /**
     * Add or change a (user, chat) tuple.
     * 
     * @param user is the user
     * @param chat is the chat that the user is active in
     */
    public void put(String user, String chat) {
        //Remove User from chat if the user is already in a chat
        if (userToChat.containsKey(user)) {
            chatToUser.get(userToChat.get(user)).remove(user);
        }

        userToChat.put(user, chat);
        Set<String> usersinchat = chatToUser.get(chat);

        if (usersinchat == null) {
            usersinchat = new HashSet<>();
            chatToUser.put(chat, usersinchat);
        }
        usersinchat.add(user);
    }

    /**
     * Gets the chat that a user is active in
     * 
     * @param user username
     * @return chatname
     */
    public String getChat(String user) {
        return userToChat.getOrDefault(user, "");
    }

    /**
     * Gets all users that are active in a chat
     * 
     * @param chat chatname
     * @return Set of usernames
     */
    public Set<String> getUsers(String chat) {
        return chatToUser.getOrDefault(chat, Collections.emptySet());
    }
}
