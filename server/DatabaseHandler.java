package server;


import java.sql.*;
import java.util.*;
import java.time.Instant;
import java.time.LocalDateTime;

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
            enableforeignkeys();
            createTablesIfNotExist();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void enableforeignkeys() {

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
                                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                     "sender TEXT NOT NULL, " +
                                     "chatname TEXT NOT NULL," + 
                                     "content TEXT, " +
                                     "FOREIGN KEY (sender) REFERENCES users(name), " +
                                     "FOREIGN KEY (chatname) REFERENCES chats(name))";

        String createChatUsersTable = "CREATE TABLE IF NOT EXISTS chat_users (" +
                                      "chatname TEXT NOT NULL, " +
                                      "username TEXT NOT NULL, " +
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

    public void addUser(User user) {

    
        String adduser = "INSERT OR IGNORE INTO users (name) VALUES (?)";
        try(PreparedStatement pstmt = connection.prepareStatement(adduser)) {
            pstmt.setString(1, user.getName());
            pstmt.executeUpdate();

        }catch(SQLException e) {
            e.printStackTrace();
        }
    }


    public void addMessage( String chatName, Message message) {
        try(PreparedStatement insertmessage = connection.prepareStatement("INSERT INTO messages ( content, sender, chatname) VALUES (?,?,?)")) {

            // insertmessage.setInt(1,null);
            insertmessage.setString(1, message.getText());
            insertmessage.setString(2, message.getUser().getName());
            insertmessage.setString(3, chatName);
            insertmessage.executeUpdate();
            System.out.println("Message added!");        

        }catch(SQLException e){
            e.printStackTrace();
        }
    } 
    

    
    private boolean checkIfUserExists(User user) {

        String check = "SELECT EXISTS( SELECT 1 FROM users where name = (?)";

        try(PreparedStatement stmt = connection.prepareStatement(check);) {
            stmt.setString(1,user.getName());
            

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return true;
            }
            
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkIfChatExists(String chat) {

        String check = "SELECT EXISTS( SELECT 1 FROM chats where name = (?)";

        try(PreparedStatement stmt = connection.prepareStatement(check);) {
            stmt.setString(1,chat);
            

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return true;
            }
            
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void getmessages(Chat chat, String chatname) {
        String getmessages = "SELECT * FROM messages WHERE chatname = (?)";

        try(PreparedStatement pstmt = connection.prepareStatement(getmessages)) {
            pstmt.setString(1, chatname);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                String message = rs.getString("content");
                String sender = rs.getString("sender");
                chat.addMessage(new Message(message, Instant.now() ,new User(0, sender)));
                
            }

        }


        catch(SQLException e){
            e.printStackTrace();
        }

    }
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
        getmessages(chat, chatname);
        return chat;
        
    }


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
    public void Connect(User user,String chat){
        if (checkIfChatExists(chat)) {
            addUserToChat(user, chat);
        }
        else {
            addChat(chat);
            addUserToChat(user, chat);
        }


    }
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



     

// All of these methods should check if the user exists else they should add them, they should do the same thing for creating/joining a chat if the
//chat is not they are trying to join does not exist then create it 

// I should add select chat from a chatlist, select user from a userlist 

// addMessage(userName, chatName) done 
// add/joinchat(chatName,userName) done 
// check if chat exists 
// disconnect(userName, chatName) done
//getChats(userName) 
//getChat(chatname) done
   



//adduser -> addUser
//sendMessage -> addMessage
//addchat -> addChat
// enableforeignkeys = true
// dropalltables removed


//add timestamp to messages 
