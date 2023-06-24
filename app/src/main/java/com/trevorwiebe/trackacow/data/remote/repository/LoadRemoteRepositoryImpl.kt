package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trevorwiebe.trackacow.domain.models.load.CacheLoadModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Provider

class LoadRemoteRepositoryImpl(
    val firebaseDatabase: FirebaseDatabase,
    var firebaseUserId: Provider<String>
) : LoadRemoteRepository {

    override fun insertOrUpdateRemoteLoad(loadModel: LoadModel) {
        val userId = firebaseUserId.get().toString()
        if (!loadModel.loadId.isNullOrEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/loads/${loadModel.loadId}"
            ).setValue(loadModel)
        }
    }

    override fun readLoadsByLotId(lotId: String): Flow<List<LoadModel>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val loadRef = firebaseDatabase
                .getReference("/users/${userId}/loads")
                .orderByChild("lotId")
                .equalTo(lotId)

            loadRef.addQueryListValueEventListenerFlow(LoadModel::class.java)
        } else {
            emptyFlow()
        }
    }


    override fun deleteRemoteLoad(loadModel: LoadModel) {
        val userId = firebaseUserId.get().toString()
        if (!loadModel.loadId.isNullOrEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/loads/${loadModel.loadId}"
            ).removeValue()
        }
    }

    override fun insertCacheLoadsRemote(loadList: List<CacheLoadModel>) {

    }

    override suspend fun updateLoadWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdsToDelete: List<String>
    ) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/loads"
            lotIdsToDelete.forEach { lotIdStr ->
                val loadQuery = firebaseDatabase
                    .getReference(databasePath)
                    .orderByChild("lotId")
                    .equalTo(lotIdStr)
                loadQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val loadModel = it.getValue(LoadModel::class.java)
                            val loadId = loadModel?.loadId
                            if (loadId != null) {
                                firebaseDatabase.getReference(databasePath).child(loadId)
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