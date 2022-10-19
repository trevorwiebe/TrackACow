package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.trevorwiebe.trackacow.data.cacheEntities.CacheUserEntity

@Keep
@Entity(tableName = "user")
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var accountType: Int = 0,
    var dateCreated: Long = 0,
    var name: String? = null,
    var email: String? = null,
    var renewalDate: Long = 0,
    var uid: String? = null

//    object UserEntityObject {
//        const val USER = "user"
//        const val FREE_TRIAL = 0
//        const val MONTHLY_SUBSCRIPTION = 1
//        const val ANNUAL_SUBSCRIPTION = 2
//        const val CANCELED = 6
//        const val FOREVER_FREE_USER = 7
//    }
)