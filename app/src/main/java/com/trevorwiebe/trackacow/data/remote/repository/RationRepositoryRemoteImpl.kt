package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.ration.CacheRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.remote.RationRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.addListValueEventListenerFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Provider

class RationRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    val firebaseUserId: Provider<String>,
): RationRepositoryRemote {

    override suspend fun insertOrUpdateRationRemote(rationModel: RationModel) {
        // make sure rationCloudDatabaseId in not null or empty
        val userId = firebaseUserId.get().toString()
        if (rationModel.rationCloudDatabaseId.isNotEmpty() && userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/rations/${rationModel.rationCloudDatabaseId}"
            firebaseDatabase.getReference(databasePath).setValue(rationModel)
        }
    }

    override suspend fun insertRationListRemote(rationModelList: List<RationModel>) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/rations/"
            firebaseDatabase.getReference(databasePath).setValue(rationModelList)
        }
    }

    override suspend fun insertCacheRationRemote(rationList: List<CacheRationModel>) {

    }

    override fun readAllRations(): Flow<List<RationModel>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val rationRef = firebaseDatabase.getReference("/users/$userId/rations")
            rationRef.addListValueEventListenerFlow(RationModel::class.java)
        } else {
            emptyFlow()
        }
    }

    override suspend fun deleteRationRemote(rationModel: RationModel) {
        val userId = firebaseUserId.get().toString()
        if (rationModel.rationCloudDatabaseId.isNotEmpty() && userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/rations/${rationModel.rationCloudDatabaseId}"
            firebaseDatabase.getReference(databasePath).removeValue()
        }
    }
}