package com.trevorwiebe.trackacow.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.trevorwiebe.trackacow.data.db.entities.RationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RationDao {

    @Insert
    suspend fun insertRation(rationEntity: RationEntity)

    @Query("SELECT * FROM ration")
    fun getRations(): Flow<List<RationEntity>>

    @Query("SELECT * FROM ration WHERE primaryKey = :id")
    fun getRationsById(id: Int): Flow<RationEntity>

    @Delete
    suspend fun deleteRation(rationEntity: RationEntity)
}