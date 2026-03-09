package client;

import java.util.Objects;

/**
 * User - Represents a chat user.
 * Stores user information for identifying message senders.
 */
public class User {
    /** Name of the user */
    private String name;

    /**
     * Constructs a new User with the specified name.
     * @param name The display name of the user.
     */
    public User(String name) {
        this.name = name;
    }

    /**
     * Returns the display name of the user.
     * @return The user's name as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets or updates the display name of the user.
     * @param name The new name to assign to this user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of the user, which is their name.
     * @return The users name.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Compares this user to another object for equality.
     * Two users are considered equal if they share the same name.
     * @param obj The object to compare with this User.
     * @return true if the names are identical; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(name, user.name);
    }

    /**
     * Generates a hash code for this user based on their name.
     * This ensures that Users can be stored and retrieved correctly.
     * @return The hash code of the user's name.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
