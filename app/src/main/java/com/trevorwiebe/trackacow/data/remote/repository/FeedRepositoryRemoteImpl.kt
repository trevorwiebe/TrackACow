package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trevorwiebe.trackacow.domain.models.feed.CacheFeedModel
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

    override suspend fun insertCacheFeeds(feedModelList: List<CacheFeedModel>) {

    }

    override suspend fun deleteFeedRemoteList(feedModelList: List<FeedModel>) {
        for (i in feedModelList.indices) {
            val feedModelId = feedModelList[i].id
            firebaseDatabase.getReference("$databasePath/$feedModelId").removeValue()
        }
    }

    override suspend fun updateFeedWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdsToDelete: List<String>
    ) {
        lotIdsToDelete.forEach { lotIdStr ->
            val feedQuery =
                firebaseDatabase.getReference(databasePath).orderByChild("lotId").equalTo(lotIdStr)
            feedQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val feedModel = it.getValue(FeedModel::class.java)
                        val feedId = feedModel?.id
                        if (feedId != null) {
                            firebaseDatabase.getReference(databasePath).child(feedId).child("lotId")
                                .setValue(lotIdToSave)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}