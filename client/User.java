package client;

/**
 * User - Represents a chat user.
 * Stores user information for identifying message senders.
 */
public class User {
    private String name;

    public User(String name) {
        this.name = name;
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
        if (obj == null || this.getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return name == user.name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
