package com.example.messenger.Model;

public class Users {
    String username, imageUrl, id, status;

    public Users() {
    }

    public Users(String username, String imageURL, String id, String status) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.id = id;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrL() {
        return imageUrl;
    }

    public void setImageUrL(String imageUrL) {
        this.imageUrl = imageUrl;
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
}
