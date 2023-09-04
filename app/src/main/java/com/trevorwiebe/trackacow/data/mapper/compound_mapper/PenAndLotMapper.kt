package com.trevorwiebe.trackacow.data.mapper.compound_mapper

import com.trevorwiebe.trackacow.data.entities.compound_entities.PenAndLotEntity
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel

fun PenAndLotEntity.toPenAndLotModel(): PenAndLotModel{
    return PenAndLotModel(
        penPrimaryKey = penPrimaryKey,
        penCloudDatabaseId = penCloudDatabaseId,
        penName = penName,
        lotPrimaryKey = lotPrimaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        rationId = rationId,
        notes = notes,
        date = date,
        archived = archived,
        dateArchived = dateArchived
    )
}

fun PenAndLotModel.toLotModel(): LotModel{
    return LotModel(
        lotPrimaryKey = lotPrimaryKey ?: 0,
        lotName = lotName ?: "",
        lotCloudDatabaseId = lotCloudDatabaseId ?: "",
        customerName = customerName,
        rationId = rationId,
        notes = notes,
        date = date ?: 0L,
        archived = archived,
        dateArchived = dateArchived,
        lotPenCloudDatabaseId = penCloudDatabaseId ?: ""
    )
}

fun PenAndLotModel.toPenModel(): PenModel {
    return PenModel(
        penPrimaryKey = penPrimaryKey,
        penName = penName,
        penCloudDatabaseId = penCloudDatabaseId
    )
}

fun PenAndLotModel.addLotModel(thisLotModel: LotModel): PenAndLotModel {
    return PenAndLotModel(
        penPrimaryKey = penPrimaryKey,
        penCloudDatabaseId = penCloudDatabaseId,
        penName = penName,
        lotPrimaryKey = thisLotModel.lotPrimaryKey,
        lotName = thisLotModel.lotName,
        lotCloudDatabaseId = thisLotModel.lotCloudDatabaseId,
        customerName = thisLotModel.customerName,
        rationId = thisLotModel.rationId,
        notes = thisLotModel.notes,
        date = thisLotModel.date,
        archived = thisLotModel.archived,
        dateArchived = thisLotModel.dateArchived
    )
}