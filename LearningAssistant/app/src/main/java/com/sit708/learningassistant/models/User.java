package com.sit708.learningassistant.models;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = "username", unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public String email;
    public String password;
    public String phone;
    public String interests; // JSON array stored as string

    public User(String username, String email, String password, String phone, String interests) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.interests = interests;
    }
}
