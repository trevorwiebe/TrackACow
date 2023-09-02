package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.cacheEntities.CacheFeedEntity
import com.trevorwiebe.trackacow.data.entities.FeedEntity
import com.trevorwiebe.trackacow.domain.models.compound_model.FeedAndRationModel
import com.trevorwiebe.trackacow.domain.models.feed.CacheFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel

fun FeedEntity.toFeedModel(): FeedModel{
    return FeedModel(
        feed = feed,
        date = date,
        id = id,
        lotId = lotId,
        rationCloudId = rationCloudId
    )
}

fun FeedModel.toFeedEntity(): FeedEntity{
    return FeedEntity(
        feed = feed,
        date = date,
        id = id,
        lotId = lotId,
        rationCloudId = rationCloudId
    )
}

fun FeedModel.toCacheFeedModel(whatHappened: Int): CacheFeedModel{
    return CacheFeedModel(
        feed = feed,
        date = date,
        id = id,
        lotId = lotId,
        rationCloudId = rationCloudId,
        whatHappened = whatHappened
    )
}

fun FeedModel.toFeedAndRationModel(rationModel: RationModel?): FeedAndRationModel {
    return FeedAndRationModel(
        feed = feed,
        date = date,
        lotId = lotId,
        rationCloudId = rationModel?.rationCloudDatabaseId,
        rationName = rationModel?.rationName
    )
}

fun CacheFeedEntity.toCacheFeedModel(): CacheFeedModel {
    return CacheFeedModel(
        feed = feed,
        date = date,
        id = id,
        lotId = lotId,
        rationCloudId = rationCloudId,
        whatHappened = whatHappened
    )
}

fun CacheFeedModel.toCacheFeedEntity(): CacheFeedEntity {
    return CacheFeedEntity(
        feed = feed,
        date = date,
        id = id,
        lotId = lotId,
        rationCloudId = rationCloudId,
        whatHappened = whatHappened
    )
}

fun CacheFeedModel.toFeedModel(): FeedModel {
    return FeedModel(
        feed = feed,
        date = date,
        id = id,
        rationCloudId = rationCloudId,
        lotId = lotId
    )
}