package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.LoadEntity
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LoadDao {

    @Query("SELECT * FROM load WHERE lotId = :lotId")
    fun readLoadsByLotId(lotId: String): Flow<List<LoadEntity>>

    // Deprecated
    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLoad(loadEntity: LoadEntity?)

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLoadList(loadEntities: List<LoadEntity?>?)

    @Deprecated("use flow return type")
    @Query("SELECT * FROM load WHERE lotId = :lotId")
    fun getLoadsByLotId(lotId: String?): List<LoadEntity?>?

    @Deprecated("use flow return type")
    @Query("SELECT * FROM load WHERE loadId = :loadId")
    fun getLoadByLoadId(loadId: String?): LoadEntity?

    @Deprecated("use suspend function")
    @Query("UPDATE load SET numberOfHead = :headCount, date = :date, description = :memo WHERE loadId = :loadId")
    fun updateLoadByFields(headCount: Int, date: Long, memo: String?, loadId: String?)

    @Deprecated("use suspend function")
    @Update
    fun updateLoad(loadEntity: LoadEntity?)

    @Deprecated("use suspend function")
    @Delete
    fun deleteLoad(loadEntity: LoadEntity?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM load")
    fun deleteLoadTable()

    @Deprecated("use suspend function")
    @Query("DELETE FROM load WHERE loadId = :loadId")
    fun deleteLoadByLoadId(loadId: String?)
}