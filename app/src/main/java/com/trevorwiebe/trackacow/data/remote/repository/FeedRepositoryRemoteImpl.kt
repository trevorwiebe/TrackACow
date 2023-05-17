package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote

class FeedRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
) : FeedRepositoryRemote {

    override suspend fun insertOrUpdateFeedRemoteList(feedModelList: List<FeedModel>) {
        feedModelList.forEach {
            if (it.id.isNotEmpty()) {
                firebaseDatabase.getReference(
                    "$databasePath/${it.id}"
                ).setValue(it)
            }
        }
    }

    override suspend fun deleteFeedRemoteList(feedModelList: List<FeedModel>) {
        for (i in feedModelList.indices) {
            val feedModelId = feedModelList[i].id
            firebaseDatabase.getReference("$databasePath/$feedModelId").removeValue()
        }
    }
}