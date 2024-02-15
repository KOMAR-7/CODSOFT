package com.example.quote_today;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insertUser(User user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserById(long userId);

    @Query("SELECT * FROM users")
    User getAllUser();

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User getUserByUsernameAndPassword(String username, String password);


}

