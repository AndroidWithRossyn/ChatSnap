package com.spacester.chatsnapsupdate.model;

public class ModelUser {
    String name, email, username, photo, id,status,typingTo;
    boolean isBlocked = false;
    public ModelUser() {
    }

    public ModelUser(String name, String email, String username, String photo, String id, String status, String typingTo, boolean isBlocked) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.photo = photo;
        this.id = id;
        this.status = status;
        this.typingTo = typingTo;
        this.isBlocked = isBlocked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
