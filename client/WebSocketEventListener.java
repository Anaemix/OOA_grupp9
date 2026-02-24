package client;

public interface WebSocketEventListener {
    void onMessageReceived(String message);
    void onConnected();
    void onDisconnected();
    void onError(String error);
}