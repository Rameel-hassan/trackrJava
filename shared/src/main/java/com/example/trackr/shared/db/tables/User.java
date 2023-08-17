package com.example.trackr.shared.db.tables;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 11:03 AM
 */

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
 public class User {
     @PrimaryKey
    public Long id;

     /**
      * A short name for the user.
      */
     public String username;

     /**
      * The [Avatar] associated with the user.
      */
     public  Avatar avatar;

    public User(Long id, String username, Avatar avatar) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }
}