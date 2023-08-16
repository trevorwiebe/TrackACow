package com.trevorwiebe.trackacow.data.mapper.compound_mapper

import com.trevorwiebe.trackacow.data.entities.compound_entities.FeedAndRationEntity
import com.trevorwiebe.trackacow.domain.models.compound_model.FeedAndRationModel

fun FeedAndRationEntity.toFeedAndRationModel(): FeedAndRationModel {
    return FeedAndRationModel(
        feed = feed,
        date = date,
        lotId = lotId,
        rationCloudId = rationCloudId,
        rationName = rationName
    )
}