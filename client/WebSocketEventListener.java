package client;

/**
 * WebSocketEventListener - Interface for handling WebSocket events.
 * Defines methods for message reception, connection status changes, and error handling.
 */
public interface WebSocketEventListener {
    /** Called when a message is received over the WebSocket connection */
    void onMessageReceived(Message message);
    /** Called when the WebSocket connection is established */
    void onConnected();
    /** Called when the WebSocket connection is closed */
    void onDisconnected();
    /** Called when an error occurs in the WebSocket connection */
    void onError(String error);
}