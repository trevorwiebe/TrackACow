package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trevorwiebe.trackacow.data.mapper.toFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.CacheFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Provider

class FeedRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    val firebaseUserId: Provider<String>
) : FeedRepositoryRemote {

    override suspend fun insertOrUpdateFeedRemoteList(feedModelList: List<FeedModel>) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            feedModelList.forEach {
                if (it.id.isNotEmpty()) {
                    firebaseDatabase.getReference(
                        "/users/${userId}/feed/${it.id}"
                    ).setValue(it)
                }
            }
        }
    }

    override suspend fun insertCacheFeeds(feedModelList: List<CacheFeedModel>) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty() && feedModelList.isNotEmpty()) {
            val feedRef = firebaseDatabase.getReference("/users/${userId}/feed")
            feedModelList.forEach { cacheFeedModel ->
                if (cacheFeedModel.whatHappened == Constants.DELETE) {
                    feedRef.child(cacheFeedModel.id).removeValue()
                } else {
                    val feedModel = cacheFeedModel.toFeedModel()
                    feedRef.child(cacheFeedModel.id).setValue(feedModel)
                }
            }
        }
    }

    override fun readFeedsByLotId(lotId: String): Flow<List<FeedModel>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val feedQuery = firebaseDatabase
                .getReference("/users/${userId}/feed")
                .orderByChild("lotId")
                .equalTo(lotId)
            feedQuery.addQueryListValueEventListenerFlow(FeedModel::class.java)
        } else {
            emptyFlow()
        }
    }

    override suspend fun deleteFeedRemoteList(feedModelList: List<FeedModel>) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            for (i in feedModelList.indices) {
                val feedModelId = feedModelList[i].id
                firebaseDatabase.getReference("/users/${userId}/feed/$feedModelId").removeValue()
            }
        }
    }

    override suspend fun updateFeedWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdsToDelete: List<String>
    ) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/feed"
            lotIdsToDelete.forEach { lotIdStr ->
                val feedQuery =
                    firebaseDatabase
                        .getReference(databasePath)
                        .orderByChild("lotId")
                        .equalTo(lotIdStr)
                feedQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val feedModel = it.getValue(FeedModel::class.java)
                            val feedId = feedModel?.id
                            if (feedId != null) {
                                firebaseDatabase.getReference(databasePath).child(feedId)
                                    .child("lotId")
                                    .setValue(lotIdToSave)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }
}