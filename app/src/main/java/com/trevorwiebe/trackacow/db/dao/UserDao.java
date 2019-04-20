package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.entities.UserEntity;

@Dao
public interface UserDao {

    @Insert
    void insertUser(UserEntity userEntity);

    @Query("SELECT * FROM user WHERE uid = :uid")
    UserEntity getUserByUid(String uid);

    @Update
    void updateUser(UserEntity userEntity);

    @Delete
    void deleteUser(UserEntity userEntity);

    @Query("DELETE FROM user")
    void deleteUserTable();

}
