package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.data.mapper.toDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.CacheDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.remote.DrugRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.addListValueEventListenerFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Provider

class DrugRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    val firebaseUserId: Provider<String>
): DrugRepositoryRemote {

    override fun insertDrug(drugModel: DrugModel) {
        val userId = firebaseUserId.get().toString()
        if (drugModel.drugCloudDatabaseId.isNotEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/drugs/${drugModel.drugCloudDatabaseId}"
            ).setValue(drugModel)
        }
    }

    override fun readDrugList(): Flow<List<DrugModel>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val drugDatabaseRef = firebaseDatabase.getReference("/users/${userId}/drugs")
            drugDatabaseRef.addListValueEventListenerFlow(DrugModel::class.java)
        } else {
            emptyFlow()
        }
    }

    override fun deleteDrug(drugModel: DrugModel) {
        val userId = firebaseUserId.get().toString()
        if (drugModel.drugCloudDatabaseId.isNotEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/drugs/${drugModel.drugCloudDatabaseId}"
            ).removeValue()
        }
    }

    override fun insertCacheDrugs(cacheDrugsList: List<CacheDrugModel>) {
        val userId = firebaseUserId.get().toString()
        if (cacheDrugsList.isNotEmpty() && userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/drugs"
            cacheDrugsList.forEach { cacheDrugModel ->
                val cacheDrugRef = firebaseDatabase.getReference(databasePath)
                if (cacheDrugModel.whatHappened == Constants.DELETE) {
                    cacheDrugRef.child(cacheDrugModel.drugCloudDatabaseId).removeValue()
                } else {
                    val drugModel = cacheDrugModel.toDrugModel()
                    cacheDrugRef.child(cacheDrugModel.drugCloudDatabaseId).setValue(drugModel)
                }
            }
        }
    }

}