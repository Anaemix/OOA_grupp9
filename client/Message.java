package client;

import java.time.Instant;

public class Message {
    private final String text;
    private final Instant time;
    private final User user;
    private final Boolean isImage;

    public Message(String text, Instant time, User user, Boolean isImage) {
        this.text = text;
        this.time = time;
        this.user = user;
        this.isImage = isImage;
    }

    public String getText() {
        return text;
    }

    public Instant getTime() {
        return time;
    }

    public Boolean isImage() {
        return isImage;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return text;
    }
}
