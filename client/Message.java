package client;

import java.time.Instant;

/**
 * Represents a single message within a chat session.
 * This class acts as a data carrier for message content, which can be either 
 * plain text or a reference to an image file, along with associated metadata.
 */
public class Message {
    /** * The content of the message. If isImage is true, this string
     * holds the filename or path to the image resource.
     */
    private final String text;
    /** The exact moment in time when the message was sent. */
    private final Instant time;
    /** The user who sent the message. */
    private final User user;
    /** Flag indicating whether the content should be rendered as an image or text. */
    private final Boolean isImage;

    /**
     * Constructs a new Message instance.
     * @param text The message body or the file path to an image.
     * @param time The timestamp of the message.
     * @param user The User who sent the message.
     * @param isImage True if the text parameter represents an image file; false for plain text.
     */
    public Message(String text, Instant time, User user, Boolean isImage) {
        this.text = text;
        this.time = time;
        this.user = user;
        this.isImage = isImage;
    }

    /**
     * Returns the text content or the image path of the message.
     * @return The message string.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the timestamp indicating when the message was sent.
     * * @return An Instant representing the time of dispatch.
     */
    public Instant getTime() {
        return time;
    }

    /**
     * Checks if this message represents an image.
     * @return True if the message is an image, false otherwise.
     */
    public Boolean isImage() {
        return isImage;
    }

    /**
     * Returns the author of the message.
     * @return The User object of the sender.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns a string representation of the message content.
     * @return The text of the message.
     */
    @Override
    public String toString() {
        return text;
    }
}
