package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.UserEntity

@Dao
interface UserDao {

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(userEntity: UserEntity?)

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserList(userEntities: List<UserEntity?>?)

    @Deprecated("use flow function")
    @Query("SELECT * FROM user WHERE uid = :uid")
    fun getUserByUid(uid: String?): UserEntity?

    @Deprecated("use suspend function")
    @Update
    fun updateUser(userEntity: UserEntity?)

    @Deprecated("use suspend function")
    @Delete
    fun deleteUser(userEntity: UserEntity?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM user")
    fun deleteUserTable()
}