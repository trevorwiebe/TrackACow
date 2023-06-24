package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trevorwiebe.trackacow.data.mapper.toCallModel
import com.trevorwiebe.trackacow.domain.models.call.CacheCallModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.combineQueryDatabaseNodes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Provider

class CallRepositoryRemoteImpl(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseUserId: Provider<String>
): CallRepositoryRemote {

    override suspend fun insertOrUpdateCallRemote(callModel: CallModel) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            if (!callModel.callCloudDatabaseId.isNullOrEmpty()) {
                firebaseDatabase.getReference(
                    "/users/${userId}/call/${callModel.callCloudDatabaseId}"
                ).setValue(callModel)
            }
        }
    }

    override suspend fun insertCallListRemote(callModelList: List<CallModel>) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            firebaseDatabase.getReference("/users/${userId}/call").setValue(callModelList)
        }
    }

    override fun readCallAndRationByLotIdRemote(lotId: String): Flow<Pair<List<RationModel>, List<CallModel>>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val callDatabaseString = "/users/${userId}/call"
            val rationDatabaseString = "/users/${userId}/rations"
            val callQuery = firebaseDatabase
                .getReference(callDatabaseString)
                .orderByChild("lotId")
                .equalTo(lotId)
            val rationsRef = firebaseDatabase.getReference(rationDatabaseString)
            combineQueryDatabaseNodes(
                rationsRef,
                callQuery,
                RationModel::class.java,
                CallModel::class.java
            )
        } else
            emptyFlow()
    }

    override suspend fun updateCallWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdToDeleteList: List<String>
    ) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/call"
            lotIdToDeleteList.forEach { lotIdStr ->
                val callQuery =
                    firebaseDatabase.getReference(databasePath).orderByChild("lotId")
                        .equalTo(lotIdStr)
                callQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val callModel = it.getValue(CallModel::class.java)
                            val callId = callModel?.callCloudDatabaseId
                            if (callId != null) {
                                firebaseDatabase.getReference(databasePath).child(callId)
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

    override suspend fun updateRemoteWithCacheCallList(cacheCallList: List<CacheCallModel>) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            if (cacheCallList.isNotEmpty()) {
                val databasePath = "/users/${userId}/call"
                cacheCallList.forEach { cacheCallModel ->
                    val cacheCallRef = firebaseDatabase.getReference(databasePath)
                    if (cacheCallModel.callCloudDatabaseId != null) {
                        if (cacheCallModel.whatHappened == Constants.DELETE) {
                            cacheCallRef.child(cacheCallModel.callCloudDatabaseId).removeValue()
                        } else {
                            val callModel = cacheCallModel.toCallModel()
                            cacheCallRef.child(cacheCallModel.callCloudDatabaseId)
                                .setValue(callModel)
                        }
                    }
                }
            }
        }
    }
}