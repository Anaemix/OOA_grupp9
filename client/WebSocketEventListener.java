package client;

public interface WebSocketEventListener {
    void onMessageReceived(Message message);
    void onConnected();
    void onDisconnected();
    void onError(String error);
}