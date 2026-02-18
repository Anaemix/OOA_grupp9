package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserChatMap {
    private final Map<String, String> userToChat = new HashMap<>();
    private final Map<String, Set<String>> chatToUser = new HashMap<>();

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

    public String getChat(String user) {
        return userToChat.getOrDefault(user, "");
    }

    public Set<String> getUsers(String chat) {
        return chatToUser.getOrDefault(chat, Collections.emptySet());
    }
}
