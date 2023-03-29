package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.FeedDao
import com.trevorwiebe.trackacow.data.mapper.toFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FeedRepositoryImpl(
    private val feedDao: FeedDao
): FeedRepository {
    override fun getFeedsByLotId(lotId: String): Flow<List<FeedModel>> {
        return feedDao.getFeedsByLotId(lotId)
            .map { feedList ->
                feedList.map { it.toFeedModel() }
            }
    }

    override fun readFeedsByDate(
        lotId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<FeedModel>> {
        return feedDao.getFeedsByDate(lotId, startDate, endDate)
            .map { feedList ->
                feedList.map { it.toFeedModel() }
            }
    }

    override suspend fun createFeedListRemote(feedModelList: List<CacheFeedModel>) {
        feedDao.insertCacheFeedEntityList(feedModelList.map { it.toCacheFeedEntity() })
    }
}