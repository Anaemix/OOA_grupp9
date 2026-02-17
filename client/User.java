package client;

/**
 * User - Represents a chat user.
 * Stores user information for identifying message senders.
 */
public class User {
    private final int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique user ID.
     */
    public int getId() {
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
        if (obj == null || this.getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
