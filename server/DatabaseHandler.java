package server;

import java.sql.*;
import java.util.*;
import java.time.Instant;
import client.User;
import client.Message;
import client.Chat;

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
            enableForeignkeys();
            createTablesIfNotExist();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void enableForeignkeys() {

        String statement = "PRAGMA foreign_keys = ON;";
        try (PreparedStatement stmt = connection.prepareStatement(statement)) {
            stmt.execute();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // Create tables if they do not exist
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
                                     "content TEXT, " +
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

    // This method removes a user from the chatusers table 
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

    //This method adds a user to the users table
    public void addUser(User user) {
        
        String adduser = "INSERT OR IGNORE INTO users (name) VALUES (?)";
        try(PreparedStatement pstmt = connection.prepareStatement(adduser)) {
            pstmt.setString(1, user.getName());
            pstmt.executeUpdate();
            
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    //This method adds a chat to the chats table
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
    //This method adds a user to chat if they are not part of the chat already
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
    //This method adds a message to the messages table 
    public void addMessage(String chatName, Message message) {        
        
        try(PreparedStatement insertmessage = connection.prepareStatement("INSERT INTO messages (timestamp, content, sender, chatname) VALUES (?,?,?,?)")) {
            
            long formattedTime = message.getTime().getEpochSecond();
            // insertmessage.setInt(1,null);
            insertmessage.setLong(1, formattedTime);
            insertmessage.setString(2, message.getText());
            insertmessage.setString(3, message.getUser().getName());
            insertmessage.setString(4, chatName);
            insertmessage.executeUpdate();
            System.out.println("Message added!");        

        }catch(SQLException e){
            e.printStackTrace();
        }
    } 
    
    //This method returns an array of all messages sent to a chat after a specific timestamp
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
                messagelist.add((new Message(message, Instant.ofEpochSecond(timestamp) ,new User(0, sender))));
            }

        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return messagelist;
    }


    // a private method used to add messages to a chat object 
    private void getMessagesInChat(Chat chat, String chatname) {
        String getMessages = "SELECT * FROM messages WHERE chatname = (?)";

        try(PreparedStatement pstmt = connection.prepareStatement(getMessages)) {
            pstmt.setString(1, chatname);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                long timestamp = rs.getInt("timestamp");
                String message = rs.getString("content");
                String sender = rs.getString("sender");
                chat.addMessage(new Message(message, Instant.ofEpochSecond(timestamp) ,new User(0, sender)));
                
            }
        }

        catch(SQLException e){
            e.printStackTrace();
        }

    }
    //This method returns a chat object
    public Chat getChat(String chatname){
    
        Chat chat = new Chat(chatname);
        String getusers = "SELECT username FROM chat_users WHERE chatname = (?)";
        try(PreparedStatement pstmt = connection.prepareStatement(getusers)) {
            pstmt.setString(1, chatname);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                String username = rs.getString("username");
                chat.addUser(new User(0, username)); 
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
        getMessagesInChat(chat, chatname);
        return chat;
    }
    //This method returns an arraylist of allchats that a user is a part of
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


// a user should be able to create a username or login
// a user should be able to join or create a chat
// no duplicate users or duplicate chats

