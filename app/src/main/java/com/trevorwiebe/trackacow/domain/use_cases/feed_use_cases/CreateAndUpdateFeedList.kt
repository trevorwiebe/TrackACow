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
            newFeedList.forEach { feedModel ->
                // iterate through newFeedList and see if any new feedModels are added
                if (!originalFeedList.contains(feedModel)) {

                    // feedModel to add or update
                    if (feedModel.id == "") {
                        // need to create new
                        feedModel.id = getCloudDatabaseId.invoke("")

                        feedListToCreate.add(feedModel)
                    }
                }
            }

            val feedListToDelete: MutableList<FeedModel> = mutableListOf()
            originalFeedList.forEach { oldFeedModel ->

                if (!newFeedList.contains(oldFeedModel)) {
                    // need to delete these feed models
                    feedListToDelete.add(oldFeedModel)
                }
            }

            if (feedListToCreate.isNotEmpty())
                feedRepository.createOrUpdateFeedList(feedListToCreate)
            if (feedListToDelete.isNotEmpty())
                feedRepository.deleteFeedList(feedListToDelete)

            if (Utility.haveNetworkConnection(context)) {

            } else {
                Utility.setNewDataToUpload(context, true)
                feedRepository.createCacheFeedList(feedListToCreate.map {
                    it.toCacheFeedModel(Constants.INSERT_UPDATE)
                })
                feedRepository.createCacheFeedList(feedListToDelete.map {
                    it.toCacheFeedModel(Constants.DELETE)
                })
            }

        }
    }
}