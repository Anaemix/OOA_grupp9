package client;

import java.time.LocalDateTime;

public class Message {
    private String text;
    private LocalDateTime time;
    private User user;

    public Message(String text, LocalDateTime time, User user) {
        this.text = text;
        this.time = time;
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return text;
    }
}
