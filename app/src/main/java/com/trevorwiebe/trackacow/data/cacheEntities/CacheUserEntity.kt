package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "cache_user")
data class CacheUserEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var accountType: Int = 0,
    var dateCreated: Long = 0,
    var name: String? = "",
    var email: String? = "",
    var renewalDate: Long = 0,
    var uid: String? = "",
    var whatHappened: Int = 0
)