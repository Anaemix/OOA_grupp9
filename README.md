# OOA_grupp9
Projektrepo för Objektorienterade applikationer

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