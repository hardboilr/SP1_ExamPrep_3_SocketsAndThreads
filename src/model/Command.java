package model;

public class Command {
    
    private String message;
    private int senderID;

    public Command(String message, int senderID) {
        this.message = message;
        this.senderID = senderID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }
}
