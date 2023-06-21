package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.pen.CachePenModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote

class PenRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): PenRepositoryRemote {

    // Use this for insert and update
    override suspend fun insertAndUpdatePenRemote(penModel: PenModel) {
        // make sure is not null or empty
        if (!penModel.penCloudDatabaseId.isNullOrEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${penModel.penCloudDatabaseId}"
            ).setValue(penModel)
        }
    }

    override suspend fun insertCachePenRemote(penList: List<CachePenModel>) {

    }

    override suspend fun deletePenRemote(penModel: PenModel) {
        // make sure is not null or empty
        if (!penModel.penCloudDatabaseId.isNullOrEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${penModel.penCloudDatabaseId}"
            ).removeValue()
        }
    }
}