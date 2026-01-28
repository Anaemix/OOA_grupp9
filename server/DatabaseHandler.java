package server;

import java.sql.*;
import java.util.*;

public class DatabaseHandler {

    private Connection connection;
    
    public DatabaseHandler() {
        connectToDatabase();
    }

    // Connect to SQLite database (database.db file)
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            System.out.println("Connected to SQLite database.");
            createTablesIfNotExist();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create tables if they do not exist
    private void createTablesIfNotExist() {
        // SQL statements to create the tables
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                                  "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                  "name TEXT NOT NULL)";
        String createChatsTable = "CREATE TABLE IF NOT EXISTS chats (" +
                                  "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                  "name TEXT NOT NULL)";
        String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages (" +
                                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                     "chat_id INTEGER, " +
                                     "user_id INTEGER, " +
                                     "message TEXT, " +
                                     "imagepath TEXT, " +
                                     "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                     "FOREIGN KEY (chat_id) REFERENCES chats(id), " +
                                     "FOREIGN KEY (user_id) REFERENCES users(id))";
        String createChatUsersTable = "CREATE TABLE IF NOT EXISTS chat_users (" +
                                      "chat_id INTEGER, " +
                                      "user_id INTEGER, " +
                                      "FOREIGN KEY (chat_id) REFERENCES chats(id), " +
                                      "FOREIGN KEY (user_id) REFERENCES users(id))";
        
        try (Statement stmt = connection.createStatement()) {
            // Execute table creation queries
            stmt.execute(createUsersTable);
            stmt.execute(createChatsTable);
            stmt.execute(createMessagesTable);
            stmt.execute(createChatUsersTable);
            System.out.println("Tables are created or already exist.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a new user to the database
    public void addUser(String userName) {
        String insertUserSQL = "INSERT INTO users (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
            pstmt.setString(1, userName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a new chat to the database
    public void addChat(String chatName) {
        String insertChatSQL = "INSERT INTO chats (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertChatSQL)) {
            pstmt.setString(1, chatName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a user to a chat
    public void addUserToChat(int userId, int chatId) {
        String insertChatUserSQL = "INSERT INTO chat_users (user_id, chat_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertChatUserSQL)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, chatId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a message to a chat
    public void addMessage(int chatId, int userId, String messageText, String imagePath) {
        String insertMessageSQL = "INSERT INTO messages (chat_id, user_id, message, imagepath) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertMessageSQL)) {
            pstmt.setInt(1, chatId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, messageText);
            pstmt.setString(4, imagePath);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get message history for a chat
    public List<String> getMessageHistory(int chatId) {
        List<String> messages = new ArrayList<>();
        String fetchMessagesSQL = "SELECT m.message, u.name, m.timestamp FROM messages m " +
                                  "JOIN users u ON m.user_id = u.id " +
                                  "WHERE m.chat_id = ? ORDER BY m.timestamp";
        try (PreparedStatement pstmt = connection.prepareStatement(fetchMessagesSQL)) {
            pstmt.setInt(1, chatId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String message = rs.getString("message");
                String user = rs.getString("name");
                String timestamp = rs.getString("timestamp");
                messages.add(user + " [" + timestamp + "]: " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    // Get a list of all chats
    public List<String> getAllChats() {
        List<String> chats = new ArrayList<>();
        String fetchChatsSQL = "SELECT id, name FROM chats";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(fetchChatsSQL);
            while (rs.next()) {
                chats.add(rs.getInt("id") + ": " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chats;
    }

    // Close the database connection
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.addUser("exampleUser");
        dbHandler.addChat("GROUP9CHAT");           
    }
} 
