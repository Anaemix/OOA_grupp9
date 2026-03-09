# OOA_grupp9
Projektrepo för Objektorienterade applikationer

FRÅN FÖRELÄSNING: VIEW BARA UPPDATERA GRAFIKEN IFRÅN VILKEN UI SOM HELST! NO COUPLING.

//ASCI art by Claude Opus 4.5
┌─────────────────────────────────────────────────────────┐
│ User Interaction                                        │
│       │                                                 │
│       ▼                                                 │
│  ┌─────────┐    events    ┌────────────┐    updates     │
│  │  View   │ ──────────►  │ Controller │ ──────────►    │
│  └─────────┘              └────────────┘                │
│       ▲                                      │          │
│       │                                      ▼          │
│       │    notifies       ┌─────────┐                   │
│       └────────────────── │  Model  │                   │
│      (Observer pattern)   └─────────┘                   │
└─────────────────────────────────────────────────────────┘

frame (JFrame)
└── leftPanel (JPanel med BorderLayout, "Chats"-border)
    ├── NORTH: addChat (textfält + knapp)
    ├── CENTER: chatListPanel (från ChatListGUI, innehåller chatt-knappar)
    └── SOUTH: login (textfält + knapp)


Server                          Client
───────                         ──────
1. GET /chat_names    →    ArrayList<String> chatNames
   (bara namnen för listan)
   
2. GET /chat/{name}   →    Chat-objekt (med ArrayList<Message>)
   (när användaren klickar)
   

## CRC

### Client

| Class | Responsibility | Collaborator |
|-|-|-|
| User | Store username, compare users (equals/hashCode) | - |
| Message | Store message text/image hash, timestamp, sender, image flag | User |
| Chat | Store chat name, message history, participant list | Message, User |
| ChatModelListener | Define observer interface for model changes (onMessageAdded, onChatsLoaded, onChatSelected, onMessageReceived, updateUserList) | Chat, Message |
| ChatModel | Store application state (current chat, chat list, user), notify listeners on state changes, create messages, communicate with server via HTTP and WebSocket | ChatModelListener, ConnectionHandler, ClientWebSocketHandler, User, Chat, Message |
| ChatView | Create and display Swing UI, receive model updates via observer callbacks, expose listeners for user interaction | ChatModelListener, ChatGUI, ChatListGUI, Chat, Message, User |
| ChatGUI | Render message history (text + images) and user list for a single chat | Chat, Message, User |
| ChatListGUI | Render sidebar panel with buttons for available chat rooms | - |
| ChatController | Handle user interaction (send message, switch chat, login, disconnect, send image), bridge View and Model, initialize WebSocket connection, application entry point main() | ChatModel, ChatView, ClientWebSocketHandler, ConnectionHandler, WebSocketEventListener, Chat, User, Message |
| ConnectionHandler | HTTP client for server REST API (Get_Chats, Get_Chat, Connect, Disconnect, Send_Message, Send_Image, Get_Image) | User, Chat, Message, Gson_InstantTypeAdapter |
| ClientWebSocketHandler | WebSocket client for real-time communication, forward incoming messages to listeners | WebSocketEventListener, Message, User, ConnectionHandler, Gson_InstantTypeAdapter |
| WebSocketEventListener | Define observer interface for WebSocket events (onMessageReceived, onConnected, onDisconnected, onError, updateUserList) | Message |

### Server

| Class | Responsibility | Collaborator |
|-|-|-|
| Server | Entry point, create HTTP server (port 2345) and WebSocket server (port 2346), register all routes/handlers | DatabaseHandler, all Handlers, WebsocketHandler |
| DatabaseHandler | SQLite persistence: create tables, CRUD for users/chats/messages/chat_users | User, Message, Chat |
| ConnectHandler | HTTP POST — add user to a chat (create user/chat if they don't exist), trigger user list broadcast | DatabaseHandler, WebsocketHandler, User |
| DisconnectHandler | HTTP POST — remove user from a chat, trigger user list broadcast | DatabaseHandler, WebsocketHandler, User |
| GetChatHandler | HTTP GET — return a complete Chat object (messages + users) as JSON | DatabaseHandler, Chat |
| GetChatsHandler | HTTP GET — return list of chat names a user belongs to | DatabaseHandler, User |
| SendMessageHandler | HTTP POST — persist message to database (deprecated, replaced by WebSocket) | DatabaseHandler, Message |
| PostImageHandler | HTTP POST — receive base64 image and save to resources/ | - |
| GetImageHandler | HTTP GET — return base64-encoded image from resources/ | - |
| WebsocketHandler | WebSocket server: handle connect/enterchat/send messages, broadcast to active users in the chat | DatabaseHandler, UserChatMap, Message, User |
| UserChatMap | Bidirectional map (user ↔ chat) for tracking which users are active in which chat via WebSocket | - |
| Gson_InstantTypeAdapter | Gson TypeAdapter for serialization/deserialization of java.time.Instant (ISO 8601) | - |




LATEX CRC NEDAN:

```latex
\documentclass{article}
\usepackage[utf8]{inputenc}
\usepackage[english]{babel}
\usepackage{longtable}
\usepackage{array}
\usepackage{booktabs}
\usepackage{geometry}
\geometry{a4paper, margin=2cm}

\title{CRC -- Chat Application}
\author{Group 9}
\date{}

\begin{document}
\maketitle

\section*{Client}

\begin{longtable}{|p{4cm}|p{6.5cm}|p{4.5cm}|}
\hline
\textbf{Class} & \textbf{Responsibility} & \textbf{Collaborator} \\
\hline
\endfirsthead
\hline
\textbf{Class} & \textbf{Responsibility} & \textbf{Collaborator} \\
\hline
\endhead

User &
Store username, compare users (equals/hashCode) &
-- \\
\hline

Message &
Store message text/image hash, timestamp, sender, image flag &
User \\
\hline

Chat &
Store chat name, message history, participant list &
Message, User \\
\hline

ChatModelListener &
Define observer interface for model changes (onMessageAdded, onChatsLoaded, onChatSelected, onMessageReceived, updateUserList) &
Chat, Message \\
\hline

ChatModel &
Store application state (current chat, chat list, user), notify listeners on state changes, create messages, communicate with server via HTTP and WebSocket &
ChatModelListener, ConnectionHandler, ClientWebSocketHandler, User, Chat, Message \\
\hline

ChatView &
Create and display Swing UI, receive model updates via observer callbacks, expose listeners for user interaction &
ChatModelListener, ChatGUI, ChatListGUI, Chat, Message, User \\
\hline

ChatGUI &
Render message history (text + images) and user list for a single chat &
Chat, Message, User \\
\hline

ChatListGUI &
Render sidebar panel with buttons for available chat rooms &
-- \\
\hline

ChatController &
Handle user interaction (send message, switch chat, login, disconnect, send image), bridge View and Model, initialize WebSocket connection, application entry point main() &
ChatModel, ChatView, ClientWebSocketHandler, ConnectionHandler, WebSocketEventListener, Chat, User, Message \\
\hline

ConnectionHandler &
HTTP client for server REST API (Get\_Chats, Get\_Chat, Connect, Disconnect, Send\_Message, Send\_Image, Get\_Image) &
User, Chat, Message, Gson\_InstantTypeAdapter \\
\hline

ClientWebSocketHandler &
WebSocket client for real-time communication, forward incoming messages to listeners &
WebSocketEventListener, Message, User, ConnectionHandler, Gson\_InstantTypeAdapter \\
\hline

WebSocketEventListener &
Define observer interface for WebSocket events (onMessageReceived, onConnected, onDisconnected, onError, updateUserList) &
Message \\
\hline

\end{longtable}

\section*{Server}

\begin{longtable}{|p{4cm}|p{6.5cm}|p{4.5cm}|}
\hline
\textbf{Class} & \textbf{Responsibility} & \textbf{Collaborator} \\
\hline
\endfirsthead
\hline
\textbf{Class} & \textbf{Responsibility} & \textbf{Collaborator} \\
\hline
\endhead

Server &
Entry point, create HTTP server (port 2345) and WebSocket server (port 2346), register all routes/handlers &
DatabaseHandler, all Handlers, WebsocketHandler \\
\hline

DatabaseHandler &
SQLite persistence: create tables, CRUD for users/chats/messages/chat\_users &
User, Message, Chat \\
\hline

ConnectHandler &
HTTP POST --- add user to a chat (create user/chat if they don't exist), trigger user list broadcast &
DatabaseHandler, WebsocketHandler, User \\
\hline

DisconnectHandler &
HTTP POST --- remove user from a chat, trigger user list broadcast &
DatabaseHandler, WebsocketHandler, User \\
\hline

GetChatHandler &
HTTP GET --- return a complete Chat object (messages + users) as JSON &
DatabaseHandler, Chat \\
\hline

GetChatsHandler &
HTTP GET --- return list of chat names a user belongs to &
DatabaseHandler, User \\
\hline

SendMessageHandler &
HTTP POST --- persist message to database (deprecated, replaced by WebSocket) &
DatabaseHandler, Message \\
\hline

PostImageHandler &
HTTP POST --- receive base64 image and save to resources/ &
-- \\
\hline

GetImageHandler &
HTTP GET --- return base64-encoded image from resources/ &
-- \\
\hline

WebsocketHandler &
WebSocket server: handle connect/enterchat/send messages, broadcast to active users in the chat &
DatabaseHandler, UserChatMap, Message, User \\
\hline

UserChatMap &
Bidirectional map (user $\leftrightarrow$ chat) for tracking which users are active in which chat via WebSocket &
-- \\
\hline

Gson\_InstantTypeAdapter &
Gson TypeAdapter for serialization/deserialization of java.time.Instant (ISO 8601) &
-- \\
\hline

\end{longtable}

\end{document}
```

