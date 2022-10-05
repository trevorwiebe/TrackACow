package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.cacheEntities.CacheFeedEntity
import com.trevorwiebe.trackacow.data.entities.FeedEntity
import com.trevorwiebe.trackacow.domain.models.feed.CacheFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel

fun FeedEntity.toFeedModel(): FeedModel{
    return FeedModel(
        primaryKey = primaryKey,
        feed = feed,
        date = date,
        id = id,
        lotId = lotId
    )
}

fun FeedModel.toFeedEntity(): FeedEntity{
    return FeedEntity(
        primaryKey = primaryKey,
        feed = feed,
        date = date,
        id = id,
        lotId = lotId
    )
}

fun FeedModel.toCacheFeedModel(whatHappened: Int): CacheFeedModel{
    return CacheFeedModel(
        primaryKey = primaryKey,
        feed = feed,
        date = date,
        id = id,
        lotId = lotId,
        whatHappened = whatHappened
    )
}

fun CacheFeedEntity.toCacheFeedModel(): CacheFeedModel{
    return CacheFeedModel(
        primaryKey = primaryKey,
        feed = feed,
        date = date,
        id = id,
        lotId = lotId,
        whatHappened = whatHappened
    )
}

fun CacheFeedModel.toCacheFeedEntity(): CacheFeedEntity{
    return CacheFeedEntity(
        primaryKey = primaryKey,
        feed = feed,
        date = date,
        id = id,
        lotId = lotId,
        whatHappened = whatHappened
    )
}