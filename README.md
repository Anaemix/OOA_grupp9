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
|Class|Responsibility|Collaborator|
|-|-|-|
|User|namn,id|-|
|Message|Store time,text,bild,sender|User|
|ChatView|Create+Display UI, receive model updates|ChatModelListener|
|ChatModel|Store messages, notify listeners of changes|ChatModelListener|
|ChatModelListener|Define observer interface for model changes|-|
|ChatController|User(clicks+input),Model(updates state)| ChatView, ChatModel|
|-|-|-|
|-|-|-|
|-|-|-|
|-|-|-|



SERVER:
|-|-|-|