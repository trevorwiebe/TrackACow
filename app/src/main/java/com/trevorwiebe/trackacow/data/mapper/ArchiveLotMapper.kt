package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.cacheEntities.CacheArchivedLotEntity
import com.trevorwiebe.trackacow.data.entities.ArchivedLotEntity
import com.trevorwiebe.trackacow.domain.models.archive_lot.ArchiveLotModel
import com.trevorwiebe.trackacow.domain.models.archive_lot.CacheArchiveLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel

fun ArchiveLotModel.toArchiveLotEntity(): ArchivedLotEntity {
    return ArchivedLotEntity(
        primaryKey = primaryKey,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        dateStarted = dateStarted,
        dateEnded = dateEnded
    )
}

//fun ArchivedLotEntity.toArchiveLotModel(): ArchiveLotModel {
//    return ArchiveLotModel(
//        primaryKey = primaryKey,
//        lotName = lotName,
//        lotId = lotId,
//        customerName = customerName,
//        notes = notes,
//        dateStarted = dateStarted,
//        dateEnded = dateEnded
//    )
//}

fun LotModel.toArchiveLotModel(dateEnded: Long): ArchiveLotModel {
    return ArchiveLotModel(
        primaryKey = lotPrimaryKey,
        lotName = lotName,
        lotId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        dateStarted = date,
        dateEnded = dateEnded
    )
}

fun ArchiveLotModel.toCacheArchiveLotModel(whatHappened: Int): CacheArchiveLotModel {
    return CacheArchiveLotModel(
        primaryKey = primaryKey,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        dateStarted = dateStarted,
        dateEnded = dateEnded,
        whatHappened = whatHappened
    )
}

fun CacheArchiveLotModel.toCacheArchiveLotEntity(): CacheArchivedLotEntity {
    return CacheArchivedLotEntity(
        primaryKey = primaryKey,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        dateStarted = dateStarted,
        dateEnded = dateEnded,
        whatHappened = whatHappened
    )
}