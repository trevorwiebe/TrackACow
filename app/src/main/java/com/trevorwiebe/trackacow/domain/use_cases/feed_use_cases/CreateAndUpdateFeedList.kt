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

    suspend operator fun invoke(feedModelList: List<FeedModel>) {

        feedModelList.map {
            if (it.id == "")
                it.id = getCloudDatabaseId.invoke("")
        }

        if (Utility.haveNetworkConnection(context)) {
            feedRepositoryRemote.insertOrUpdateFeedRemoteList(feedModelList)
        } else {
            Utility.setNewDataToUpload(context, true)
            feedRepository.createFeedListRemote(feedModelList.map {
                it.toCacheFeedModel(Constants.INSERT_UPDATE)
            })
        }
    }
}