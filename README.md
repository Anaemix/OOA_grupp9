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
| User | Lagra användarnamn, jämföra användare (equals/hashCode) | - |
| Message | Lagra meddelandetext/bildhash, tidsstämpel, avsändare, bildflagga | User |
| Chat | Lagra chattnamn, meddelandehistorik, deltagarlista | Message, User |
| ChatModelListener | Definiera observer-interface för modelländringar (onMessageAdded, onChatsLoaded, onChatSelected, onMessageReceived) | Chat, Message |
| ChatModel | Lagra applikationstillstånd (aktuell chatt, chattlista, användare), notifiera lyssnare vid ändringar, skapa meddelanden, kommunicera med server via HTTP | ChatModelListener, ConnectionHandler, User, Chat, Message |
| ChatView | Skapa och visa Swing-UI, ta emot modellupdateringar via observer-callbacks, exponera lyssnare för användarinteraktion | ChatModelListener, ChatGUI, ChatListGUI, Chat, Message |
| ChatGUI | Rendera meddelandehistorik (text + bilder) och användarlista för en enskild chatt | Chat, Message, User |
| ChatListGUI | Rendera sidopanel med knappar för tillgängliga chattrum | - |
| ChatController | Hantera användarinteraktion (skicka meddelande, byta chatt, login, disconnect, skicka bild), brygga mellan View och Model, initiera WebSocket-anslutning, applikationens main() | ChatModel, ChatView, ClientWebSocketHandler, ConnectionHandler, WebSocketEventListener, Chat, User, Message |
| ConnectionHandler | HTTP-klient mot serverns REST API (Get_Chats, Get_Chat, Connect, Disconnect, Send_Message, Send_Image, Get_Image) | User, Chat, Message, Gson_InstantTypeAdapter |
| ClientWebSocketHandler | WebSocket-klient för realtidskommunikation, vidarebefordra inkommande meddelanden till lyssnare | WebSocketEventListener, Message, Gson_InstantTypeAdapter |
| WebSocketEventListener | Definiera observer-interface för WebSocket-events (onMessageReceived, onConnected, onDisconnected, onError) | Message |

### Server

| Class | Responsibility | Collaborator |
|-|-|-|
| Server | Entry point, skapa HTTP-server (port 2345) och WebSocket-server (port 2346), registrera alla routes/handlers | DatabaseHandler, alla Handlers, WebsocketHandler |
| DatabaseHandler | SQLite-persistens: skapa tabeller, CRUD för users/chats/messages/chat_users | User, Message, Chat |
| ConnectHandler | HTTP POST — lägg till användare i en chatt (skapa user/chat om de inte finns) | DatabaseHandler, User |
| DisconnectHandler | HTTP POST — ta bort användare från en chatt | DatabaseHandler, User |
| GetChatHandler | HTTP GET — returnera ett komplett Chat-objekt (meddelanden + användare) som JSON | DatabaseHandler, Chat |
| GetChatsHandler | HTTP GET — returnera lista av chattnamn som en användare tillhör | DatabaseHandler, User |
| SendMessageHandler | HTTP POST — spara meddelande i databas (deprecated, ersatt av WebSocket) | DatabaseHandler, Message |
| PostImageHandler | HTTP POST — ta emot base64-bild och spara till resources/ | DatabaseHandler |
| GetImageHandler | HTTP GET — returnera base64-kodad bild från resources/ | DatabaseHandler |
| WebsocketHandler | WebSocket-server: hantera connect/enterchat/send-meddelanden, broadcasta till aktiva användare i chatten | DatabaseHandler, UserChatMap, Message |
| UserChatMap | Dubbelriktad map (user ↔ chat) för att spåra vilka användare som är aktiva i vilken chatt via WebSocket | - |
| Gson_InstantTypeAdapter | Gson TypeAdapter för serialisering/deserialisering av java.time.Instant (ISO 8601) | - |