package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.cacheEntities.CachePenEntity
import com.trevorwiebe.trackacow.data.entities.PenEntity
import com.trevorwiebe.trackacow.domain.models.pen.CachePenModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel

fun PenModel.toPenEntity(): PenEntity {
    return PenEntity(
        penPrimaryKey = penPrimaryKey,
        penCloudDatabaseId = penCloudDatabaseId,
        penName = penName
    )
}

fun PenEntity.toPenModel(): PenModel {
    return PenModel(
        penPrimaryKey = penPrimaryKey,
        penCloudDatabaseId = penCloudDatabaseId,
        penName = penName
    )
}

fun PenModel.toCachePenModel(whatHappened: Int): CachePenModel {
    return CachePenModel(
        primaryKey = penPrimaryKey,
        penCloudDatabaseId = penCloudDatabaseId,
        penName = penName,
        whatHappened = whatHappened
    )
}

fun CachePenModel.toCachePenEntity(): CachePenEntity{
    return CachePenEntity(
        primaryKey = primaryKey,
        penCloudDatabaseId = penCloudDatabaseId,
        penName = penName,
        whatHappened = whatHappened
    )
}

fun CachePenEntity.toCachePenModel(): CachePenModel{
    return CachePenModel(
        primaryKey = primaryKey,
        penCloudDatabaseId = penCloudDatabaseId,
        penName = penName,
        whatHappened = whatHappened
    )
}
