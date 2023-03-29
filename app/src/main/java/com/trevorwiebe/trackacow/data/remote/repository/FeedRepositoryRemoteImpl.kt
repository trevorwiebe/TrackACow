package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote

class FeedRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
) : FeedRepositoryRemote {

    override suspend fun insertOrUpdateFeedRemoteList(feedModelList: List<FeedModel>) {

    }

}