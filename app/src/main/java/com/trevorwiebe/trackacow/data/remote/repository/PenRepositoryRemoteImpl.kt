package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.data.mapper.toPenModel
import com.trevorwiebe.trackacow.domain.models.pen.CachePenModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import javax.inject.Provider

class PenRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    val firebaseUserId: Provider<String>
): PenRepositoryRemote {

    // Use this for insert and update
    override suspend fun insertAndUpdatePenRemote(penModel: PenModel) {
        val userId = firebaseUserId.get().toString()
        // make sure is not null or empty
        if (!penModel.penCloudDatabaseId.isNullOrEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/pens/${penModel.penCloudDatabaseId}"
            ).setValue(penModel)
        }
    }

    override suspend fun insertCachePenRemote(penList: List<CachePenModel>) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty() && penList.isNotEmpty()) {
            val penRef = firebaseDatabase.getReference("/users/${userId}/pens")
            penList.forEach { cachePenModel ->
                if (cachePenModel.penCloudDatabaseId != null) {
                    if (cachePenModel.whatHappened == Constants.DELETE) {
                        penRef.child(cachePenModel.penCloudDatabaseId!!).removeValue()
                    } else {
                        val penModel = cachePenModel.toPenModel()
                        penRef.child(cachePenModel.penCloudDatabaseId!!).setValue(penModel)
                    }
                }
            }
        }
    }

    override suspend fun deletePenRemote(penModel: PenModel) {
        val userId = firebaseUserId.get().toString()
        // make sure is not null or empty
        if (!penModel.penCloudDatabaseId.isNullOrEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/pens/${penModel.penCloudDatabaseId}"
            ).removeValue()
        }
    }
}