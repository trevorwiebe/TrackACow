package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class CreateAndUpdateFeedList(
    private val feedRepository: FeedRepository,
    private val feedRepositoryRemote: FeedRepositoryRemote,
    private val getCloudDatabaseId: GetCloudDatabaseId,
    private val context: Application
) {

    suspend operator fun invoke(originalFeedList: List<FeedModel>, newFeedList: List<FeedModel>) {

        // check if there is any difference between the original and the new
        if (
            originalFeedList.size != newFeedList.size ||
            originalFeedList.toSet() != newFeedList.toSet()
        ) {

            val feedListToCreate: MutableList<FeedModel> = mutableListOf()
            val feedListToUpdate: MutableList<FeedModel> = mutableListOf()
            newFeedList.forEach { feedModel ->
                // iterate through newFeedList and see if any new feedModels are added
                if (!originalFeedList.contains(feedModel)) {

                    // feedModel to add or update
                    if (feedModel.id == "") {
                        // need to create new
                        feedModel.id = getCloudDatabaseId.invoke("")

                        feedListToCreate.add(feedModel)
                    } else {
                        feedListToUpdate.add(feedModel)
                    }
                }
            }

            val feedListToDelete: MutableList<FeedModel> = mutableListOf()
            val feedListUpdateIds = feedListToUpdate.map { it.id }
            originalFeedList.forEach { oldFeedModel ->

                if (!newFeedList.contains(oldFeedModel) && !feedListUpdateIds.contains(oldFeedModel.id)) {
                    // need to delete these feed models
                    feedListToDelete.add(oldFeedModel)
                }
            }

            // Add, update or Delete feed list from local DB
            feedRepository.createOrUpdateFeedList(feedListToCreate)
            feedRepository.deleteFeedList(feedListToDelete)

            if (Utility.haveNetworkConnection(context)) {
                feedRepositoryRemote.insertOrUpdateFeedRemoteList(feedListToCreate)
                feedRepositoryRemote.insertOrUpdateFeedRemoteList(feedListToUpdate)
                feedRepositoryRemote.deleteFeedRemoteList(feedListToDelete)
            } else {
                Utility.setNewDataToUpload(context, true)
                feedRepository.createCacheFeedList(feedListToCreate.map {
                    it.toCacheFeedModel(Constants.INSERT_UPDATE)
                })
                feedRepository.createCacheFeedList(feedListToUpdate.map {
                    it.toCacheFeedModel(Constants.INSERT_UPDATE)
                })
                feedRepository.createCacheFeedList(feedListToDelete.map {
                    it.toCacheFeedModel(Constants.DELETE)
                })
            }
        }
    }
}