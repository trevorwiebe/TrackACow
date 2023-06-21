package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trevorwiebe.trackacow.data.mapper.toCallModel
import com.trevorwiebe.trackacow.domain.models.call.CacheCallModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants

class CallRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): CallRepositoryRemote {

    override suspend fun insertOrUpdateCallRemote(callModel: CallModel) {
        if (!callModel.callCloudDatabaseId.isNullOrEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${callModel.callCloudDatabaseId}"
            ).setValue(callModel)
        }
    }

    override suspend fun insertCallListRemote(callModelList: List<CallModel>) {
        firebaseDatabase.getReference(databasePath).setValue(callModelList)
    }

    override suspend fun updateCallWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdToDeleteList: List<String>
    ) {
        lotIdToDeleteList.forEach { lotIdStr ->
            val callQuery =
                firebaseDatabase.getReference(databasePath).orderByChild("lotId").equalTo(lotIdStr)
            callQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val callModel = it.getValue(CallModel::class.java)
                        val callId = callModel?.callCloudDatabaseId
                        if (callId != null) {
                            firebaseDatabase.getReference(databasePath).child(callId).child("lotId")
                                .setValue(lotIdToSave)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    override suspend fun updateRemoteWithCacheCallList(cacheCallList: List<CacheCallModel>) {
        if (cacheCallList.isNotEmpty()) {
            cacheCallList.forEach { cacheCallModel ->
                val cacheCallRef = firebaseDatabase.getReference(databasePath)
                if (cacheCallModel.callCloudDatabaseId != null) {
                    if (cacheCallModel.whatHappened == Constants.DELETE) {
                        cacheCallRef.child(cacheCallModel.callCloudDatabaseId).removeValue()
                    } else {
                        val callModel = cacheCallModel.toCallModel()
                        cacheCallRef.child(cacheCallModel.callCloudDatabaseId).setValue(callModel)
                    }
                }
            }
        }
    }
}