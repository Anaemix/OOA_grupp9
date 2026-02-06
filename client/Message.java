package client;

import java.time.Instant;

public class Message {
    private final String text;
    private final Instant time;
    private final User user;

    public Message(String text, Instant time, User user) {
        this.text = text;
        this.time = time;
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public Instant getTime() {
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
