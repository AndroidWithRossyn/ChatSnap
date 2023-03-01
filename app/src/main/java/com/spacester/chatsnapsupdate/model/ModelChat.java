package com.spacester.chatsnapsupdate.model;

public class ModelChat  {

    private String sender;
    private String receiver;
    private String msg;
    private String type;
    private String timestamp;
    private boolean isSeen;

    public ModelChat(String sender, String receiver, String msg, String type,String timestamp, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.type = type;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    public ModelChat() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }
}
