package kryword.recuperalo.Modelos;

import com.google.firebase.database.ServerValue;

public class Message {
    private String sender;
    private String chatId;
    private String message;
    private Object timestamp = ServerValue.TIMESTAMP;

    public Message(String sender, String chatId, String message, Object timestamp) {
        this.sender = sender;
        this.chatId = chatId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Message() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return sender + ": " + message;
    }
}
