package com.trevorwiebe.trackacow.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.db.entities.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insertUser(UserEntity userEntity);

    @Insert
    void insertUserList(List<UserEntity> userEntities);

    @Query("SELECT * FROM user WHERE uid = :uid")
    UserEntity getUserByUid(String uid);

    @Update
    void updateUser(UserEntity userEntity);

    @Delete
    void deleteUser(UserEntity userEntity);

    @Query("DELETE FROM user")
    void deleteUserTable();

}
