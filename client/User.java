package client;

/**
 * User - Represents a chat user.
 * Stores user information for identifying message senders.
 */
public class User {
    private final String id;
    private String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique user ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the display name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
