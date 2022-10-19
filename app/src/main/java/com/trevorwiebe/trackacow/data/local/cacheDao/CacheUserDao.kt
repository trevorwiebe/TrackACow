package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheUserEntity

@Dao
interface CacheUserDao {

    @Deprecated("use suspend function")
    @Insert
    fun insertHoldingUser(cacheUserEntity: CacheUserEntity?)

    @Deprecated("use flow function")
    @Query("SELECT * FROM holdingUser WHERE uid = :uid")
    fun getHoldingUserByUid(uid: String?): CacheUserEntity?

    @Deprecated("use flow function")
    @Query("SELECT * FROM holdingUser")
    fun getHoldingUserList(): List<CacheUserEntity?>?

    @Deprecated("use suspend function")
    @Update
    fun updateHoldingUser(cacheUserEntity: CacheUserEntity?)

    @Deprecated("use suspend function")
    @Delete
    fun deleteHoldingUser(cacheUserEntity: CacheUserEntity?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM holdingUser")
    fun deleteHoldingUserTable()
}