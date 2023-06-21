package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trevorwiebe.trackacow.domain.models.load.CacheLoadModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository

class LoadRemoteRepositoryImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
) : LoadRemoteRepository {

    override fun insertOrUpdateRemoteLoad(loadModel: LoadModel) {
        if (!loadModel.loadId.isNullOrEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${loadModel.loadId}"
            ).setValue(loadModel)
        }
    }


    override fun deleteRemoteLoad(loadModel: LoadModel) {
        if (!loadModel.loadId.isNullOrEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${loadModel.loadId}"
            ).removeValue()
        }
    }

    override fun insertCacheLoadsRemote(loadList: List<CacheLoadModel>) {

    }

    override suspend fun updateLoadWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdsToDelete: List<String>
    ) {
        lotIdsToDelete.forEach { lotIdStr ->
            val loadQuery =
                firebaseDatabase.getReference(databasePath).orderByChild("lotId").equalTo(lotIdStr)
            loadQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val loadModel = it.getValue(LoadModel::class.java)
                        val loadId = loadModel?.loadId
                        if (loadId != null) {
                            firebaseDatabase.getReference(databasePath).child(loadId).child("lotId")
                                .setValue(lotIdToSave)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

}