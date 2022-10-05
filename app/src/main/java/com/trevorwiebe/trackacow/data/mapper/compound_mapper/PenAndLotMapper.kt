package com.trevorwiebe.trackacow.data.mapper.compound_mapper

import com.trevorwiebe.trackacow.data.entities.compound_entities.PenAndLotEntity
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel

fun PenAndLotEntity.toPenAndLotModel(): PenAndLotModel{
    return PenAndLotModel(
        penId = penPenId,
        penName = penName,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        date = date
    )
}

fun PenAndLotModel.toPenAndLotEntity(): PenAndLotEntity{
    return PenAndLotEntity(
        penPenId = penId,
        penName = penName,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        date = date
    )
}

fun PenAndLotModel.toLotModel(): LotModel{
    return LotModel(
        lotName = lotName ?: "",
        lotId = lotId ?: "",
        customerName = customerName,
        notes = notes,
        date = date ?: 0L
    )
}