package server;

import java.sql.*;
import java.util.*;
import java.time.Instant;
import client.User;
import client.Message;
import client.Chat;

/**
 * Handles all database operations for the chat server.
 * <p>
 * This class manages the connection to a local SQLite database and provides
 * methods for creating tables, inserting and retrieving users, chats,
 * and messages.
 * </p>
 *
 * <p>
 * The database schema consists of four tables:
 * <ul>
 *   <li><b>users</b> – Stores registered usernames.</li>
 *   <li><b>chats</b> – Stores available chat rooms.</li>
 *   <li><b>messages</b> – Stores messages sent in chats.</li>
 *   <li><b>chat_users</b> – Maps users to the chats they participate in.</li>
 * </ul>
 * </p>
 *
 * Foreign key constraints are enabled to maintain referential integrity.
 *
 * @author Najib
 * @version 1.0
 */

public class DatabaseHandler {

    private Connection connection;
    
    public DatabaseHandler() {
        connectToDatabase();
    }
  
    /**
    * Establishes a connection to the SQLite database file.
    * <p>
    * Enables Write-Ahead Logging (WAL) mode and sets a busy timeout.
    * Also ensures foreign key constraints are active and required tables exist.
    * </p>
    */
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db?journal_mode=WAL&busy_timeout=5000");
            System.out.println("Connected to SQLite database.");
            enableForeignkeys();
            createTablesIfNotExist();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
    * Enables SQLite foreign key constraints.
    * <p>
    * This ensures referential integrity between related tables.
    * </p>
    */
    private void enableForeignkeys() {

        String statement = "PRAGMA foreign_keys = ON;";
        try (PreparedStatement stmt = connection.prepareStatement(statement)) {
            stmt.execute();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
    * Creates all required database tables if they do not already exist.
    * <p>
    * Tables created:
    * <ul>
    *   <li>users</li>
    *   <li>chats</li>
    *   <li>messages</li>
    *   <li>chat_users</li>
    * </ul>
    * </p>
    */
    private void createTablesIfNotExist() {
        
        
        // SQL statements to create the tables
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                                  "name TEXT PRIMARY KEY)";

        String createChatsTable = "CREATE TABLE IF NOT EXISTS chats (" +
                                  "name TEXT PRIMARY KEY)";

        String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages (" +
                                     "timestamp INTEGER NOT NULL," +
                                     "sender TEXT NOT NULL, " +
                                     "chatname TEXT NOT NULL," + 
                                     "content TEXT," +
                                     "isImage INTEGER," +
                                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                     "FOREIGN KEY (sender) REFERENCES users(name), " +
                                     "FOREIGN KEY (chatname) REFERENCES chats(name))";

        String createChatUsersTable = "CREATE TABLE IF NOT EXISTS chat_users (" +
                                      "chatname TEXT  NOT NULL, " +
                                      "username TEXT  NOT NULL, " +
                                      "PRIMARY KEY (username , chatname)," +
                                      "FOREIGN KEY (chatname) REFERENCES chats(name), " +
                                      "FOREIGN KEY (username) REFERENCES users(name))";
        
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

    /**
    * Closes the active database connection.
    * <p>
    * Should be called when the server shuts down to release resources.
    * </p>
    */
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

    /**
    * Removes a user from a specific chat.
    *
    * @param user     the user to remove
    * @param chatName the name of the chat
    */
    public void removeUserFromChat(User user, String chatName) {
        String remove = "DELETE FROM chat_users WHERE username = ? AND chatname = (?)";
        try(PreparedStatement pstmt = connection.prepareStatement(remove)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, chatName);
            pstmt.executeUpdate();
            System.out.println("user removed from chat");

        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
    * Adds a user to the database.
    * <p>
    * If the user already exists, the operation is ignored.
    * </p>
    *
    * @param user the user to add
    */
    public void addUser(User user) {
        
        String adduser = "INSERT OR IGNORE INTO users (name) VALUES (?)";
        try(PreparedStatement pstmt = connection.prepareStatement(adduser)) {
            pstmt.setString(1, user.getName());
            pstmt.executeUpdate();
            
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
    * Adds a new chat to the database.
    * <p>
    * If the chat already exists, the operation is ignored.
    * </p>
    *
    * @param chatname the name of the chat
    */
    public void addChat(String chatname) {
        String addchat = "INSERT OR IGNORE INTO chats (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(addchat)) {
            pstmt.setString(1, chatname);
            pstmt.executeUpdate();
            System.out.println("chat added to chats table");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
    * Adds a user to a specific chat.
    * <p>
    * If the user is already a member of the chat, the operation is ignored.
    * </p>
    *
    * @param user     the user to add
    * @param chatname the name of the chat
    */
    public void addUserToChat(User user, String chatname) {
        String adduser = "INSERT OR IGNORE INTO chat_users (username,chatname) VALUES (?,?)"; 

        try(PreparedStatement stmt = connection.prepareStatement(adduser)){
            stmt.setString(1, user.getName());
            stmt.setString(2, chatname);
            stmt.executeUpdate();

        }
            
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
    * Inserts a message into the database.
    *
    * @param chatName the name of the chat where the message was sent
    * @param message  the message object containing content, sender, timestamp,
    *                 and image flag
    */
    public void addMessage(String chatName, Message message) {        

        try(PreparedStatement insertmessage = connection.prepareStatement("INSERT INTO messages (timestamp, content, sender, chatname, isImage) VALUES (?,?,?,?,?)")) {
            
            long formattedTime = message.getTime().getEpochSecond();
            // insertmessage.setInt(1,null);
            insertmessage.setLong(1, formattedTime);
            insertmessage.setString(2, message.getText());
            insertmessage.setString(3, message.getUser().getName());
            insertmessage.setString(4, chatName);
            insertmessage.setBoolean(5, message.isImage());
            insertmessage.executeUpdate();
            System.out.println("Message added!");        

        }catch(SQLException e){
            e.printStackTrace();
        }
    } 
    
    /**
    * Retrieves all messages from a specific chat that were sent
    * after a given timestamp.
    *
    * @param chatname the name of the chat
    * @param time     only messages sent after this time are returned
    * @return a list of messages ordered by timestamp (ascending)
    */
    public ArrayList<Message> getMessages(String chatname, Instant time){

        ArrayList<Message> messagelist = new ArrayList<Message>();
        try(PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM messages WHERE chatname = ? AND timestamp > (?) ORDER BY timestamp ASC")) {
            pstmt.setString(1,chatname);
            pstmt.setLong(2, time.getEpochSecond());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                long timestamp = rs.getLong("timestamp");
                String message = rs.getString("content");
                String sender = rs.getString("sender");
                boolean isImage = rs.getBoolean("isImage");
                messagelist.add((new Message(message, Instant.ofEpochSecond(timestamp) ,new User(sender), isImage)));
            }

        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return messagelist;
    }


    /**
    * Retrieves all messages belonging to a specific chat and
    * adds them to the provided Chat object.
    *
    * @param chat     the chat object to populate
    * @param chatname the name of the chat
    */ 
    private void getMessagesInChat(Chat chat, String chatname) {
        String getMessages = "SELECT * FROM messages WHERE chatname = (?)";

        try(PreparedStatement pstmt = connection.prepareStatement(getMessages)) {
            pstmt.setString(1, chatname);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                long timestamp = rs.getInt("timestamp");
                String message = rs.getString("content");
                String sender = rs.getString("sender");
                boolean isImage = rs.getBoolean("isImage");
                chat.addMessage(new Message(message, Instant.ofEpochSecond(timestamp) ,new User(sender), isImage));
                
            }
        }

        catch(SQLException e){
            e.printStackTrace();
        }

    }
    /**
    * Retrieves a complete Chat object from the database.
    * <p>
    * The returned Chat includes:
    * <ul>
    *   <li>All users in the chat</li>
    *   <li>All messages in the chat</li>
    * </ul>
    * </p>
    *
    * @param chatname the name of the chat
    * @return a fully populated Chat object
    */
    public Chat getChat(String chatname){
    
        Chat chat = new Chat(chatname);
        String getusers = "SELECT username FROM chat_users WHERE chatname = (?)";
        try(PreparedStatement pstmt = connection.prepareStatement(getusers)) {
            pstmt.setString(1, chatname);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                String username = rs.getString("username");
                chat.addUser(new User(username)); 
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
        getMessagesInChat(chat, chatname);
        return chat;
    }
    /**
    * Retrieves the names of all chats that a user participates in.
    *
    * @param user the user whose chats should be retrieved
    * @return a list of chat names
    */
       public  ArrayList<String> getAllChats(User user){
        ArrayList<String> list = new ArrayList<>();
        String getchatname = "SELECT chatname FROM chat_users where username = (?)";

        try(PreparedStatement pstmt = connection.prepareStatement(getchatname)) {
            pstmt.setString(1, user.getName());
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {

                String chatname = rs.getString("chatname");
                list.add(chatname);
            }
        }   
        catch(SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        

    }

    
}


