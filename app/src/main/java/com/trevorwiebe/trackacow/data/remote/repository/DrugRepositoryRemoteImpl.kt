package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.data.mapper.toDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.CacheDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.remote.DrugRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants

class DrugRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): DrugRepositoryRemote {

    override fun insertDrug(drugModel: DrugModel) {
        if (drugModel.drugCloudDatabaseId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath${drugModel.drugCloudDatabaseId}"
            ).setValue(drugModel)
        }
    }

    override fun deleteDrug(drugModel: DrugModel) {
        if (drugModel.drugCloudDatabaseId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath${drugModel.drugCloudDatabaseId}"
            ).removeValue()
        }
    }

    override fun insertCacheDrugs(cacheDrugsList: List<CacheDrugModel>) {
        if (cacheDrugsList.isNotEmpty()) {
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