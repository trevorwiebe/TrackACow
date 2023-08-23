package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.cacheDao.CacheFeedDao
import com.trevorwiebe.trackacow.data.local.dao.FeedDao
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toFeedAndRationModel
import com.trevorwiebe.trackacow.data.mapper.toCacheFeedEntity
import com.trevorwiebe.trackacow.data.mapper.toCacheFeedModel
import com.trevorwiebe.trackacow.data.mapper.toFeedEntity
import com.trevorwiebe.trackacow.data.mapper.toFeedModel
import com.trevorwiebe.trackacow.domain.models.compound_model.FeedAndRationModel
import com.trevorwiebe.trackacow.domain.models.feed.CacheFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FeedRepositoryImpl(
    private val feedDao: FeedDao,
    private val cacheFeedDao: CacheFeedDao
): FeedRepository {

    override suspend fun createOrUpdateFeedList(feedModelList: List<FeedModel>): List<Long> {
        return feedDao.insertFeedEntityList(feedModelList.map { it.toFeedEntity() })
    }

    override fun getFeedsByLotId(lotId: String): Flow<List<FeedModel>> {
        return feedDao.getFeedsByLotId(lotId)
            .map { feedList ->
                feedList.map { it.toFeedModel() }
            }
    }

    override fun readFeedsByLotIdAndDate(
            lotId: String,
            startDate: Long,
            endDate: Long
    ): Flow<List<FeedModel>> {
        return feedDao.getFeedsByLotIdAndDate(lotId, startDate, endDate)
                .map { feedList ->
                    feedList.map { it.toFeedModel() }
                }
    }

    override fun readFeedsAndRationTotalByLotIdAndDate(lotId: String, startDate: Long, endDate: Long): Flow<List<FeedAndRationModel>> {
        return feedDao.getFeedsAndRationsTotalsByLotIdAndDate(lotId, startDate, endDate)
                .map { feedList ->
                    feedList.map { it.toFeedAndRationModel() }
                }
    }

    override suspend fun updateFeedsWithNewLot(lotId: String, oldLotIds: List<String>) {
        feedDao.updateFeedsWithNewLotId(lotId, oldLotIds)
    }

    override suspend fun deleteFeedList(feedModelList: List<FeedModel>) {
        val idList = feedModelList.map { it.id }
        feedDao.deleteFeedByIdList(idList)
    }

    override suspend fun deleteAllFeeds() {
        feedDao.deleteAllFeeds()
    }

    override suspend fun syncCloudFeedByLotId(feedModelList: List<FeedModel>, lotId: String) {
        feedDao.syncCloudFeedByLotId(feedModelList.map { it.toFeedEntity() }, lotId)
    }

    override suspend fun syncCloudFeedByLotIdAndDate(
        feedModelList: List<FeedModel>,
        lotId: String,
        startDate: Long,
        endDate: Long
    ) {
        feedDao.syncCloudFeedByLotIdAndDate(
            feedModelList.map { it.toFeedEntity() },
            lotId, startDate, endDate
        )
    }

    override suspend fun createCacheFeedList(feedModelList: List<CacheFeedModel>) {
        cacheFeedDao.insertHoldingFeedList(feedModelList.map { it.toCacheFeedEntity() })
    }

    override suspend fun getCacheFeeds(): List<CacheFeedModel> {
        return cacheFeedDao.getCacheFeeds().map { it.toCacheFeedModel() }
    }

    override suspend fun deleteCacheFeeds() {
        cacheFeedDao.deleteCacheDrugs()
    }
}