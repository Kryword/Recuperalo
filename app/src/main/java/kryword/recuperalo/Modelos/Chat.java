package kryword.recuperalo.Modelos;

public class Chat {
    String id;
    String sender;
    String senderName;
    String receiver;
    String receiverName;
    String topic;

    public Chat(String id, String sender, String senderName, String receiver, String receiverName, String topic) {
        this.id = id;
        this.sender = sender;
        this.senderName = senderName;
        this.receiver = receiver;
        this.receiverName = receiverName;
        this.topic = topic;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Chat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "Chat entre " + senderName + " y " + receiverName + " sobre " + topic;
    }
}
