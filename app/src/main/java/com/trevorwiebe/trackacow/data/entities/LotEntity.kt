package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity
import com.trevorwiebe.trackacow.data.entities.ArchivedLotEntity

@Keep
@Entity(tableName = "lot")
class LotEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var lotName: String? = null,
    var lotId: String? = null,
    var customerName: String? = null,
    var notes: String? = null,
    var date: Long = 0,
    var penId: String? = null
)