package com.example.messenger.Model;

import com.google.firebase.database.Exclude;

public class Chats {

    private String sender,reciver,message,imagePost,key;

    public Chats() {

    }

    public Chats(String sender, String reciver, String message,String imagePost) {
        this.sender = sender;
        this.reciver = reciver;
        this.message = message;
        this.imagePost=imagePost;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImagePost() {
        return imagePost;
    }

    public void setImagePost(String imagePost) {
        this.imagePost = imagePost;
    }
}
