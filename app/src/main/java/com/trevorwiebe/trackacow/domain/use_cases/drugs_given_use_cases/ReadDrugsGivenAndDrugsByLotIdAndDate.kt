package com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.data.mapper.toDrugGivenAndDrug
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import com.trevorwiebe.trackacow.domain.utils.combineQueryDatabaseNodes
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class ReadDrugsGivenAndDrugsByLotIdAndDate(
    private val drugRepository: DrugRepository,
    private val drugsGivenRepository: DrugsGivenRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val drugDatabaseString: String,
    private val drugsGivenDatabaseString: String
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String, startDate: Long, endDate: Long):
            Flow<List<DrugsGivenAndDrugModel>> {
        val localFlow =
            drugsGivenRepository.getDrugsGivenAndDrugsByLotIdAndDate(lotId, startDate, endDate)

        val drugRef = firebaseDatabase.getReference(drugDatabaseString)
        val drugGivenQuery = firebaseDatabase
            .getReference(drugsGivenDatabaseString)
            .orderByChild("drugsGivenLotId")
            .equalTo(lotId)

        return localFlow
            .flatMapConcat { localData ->
                combineQueryDatabaseNodes(
                    drugRef,
                    drugGivenQuery,
                    DrugModel::class.java,
                    DrugGivenModel::class.java
                ).flatMapConcat { pair ->
                    drugRepository.insertOrUpdateDrugList(pair.first)
                    drugsGivenRepository.insertOrUpdateDrugGivenList(pair.second)
                    flow {
                        val combinedList =
                            combineDrugList(pair.first, pair.second, startDate, endDate)
                        emit(combinedList)
                    }
                }.onStart { emit(localData) }
            }
    }

    private fun combineDrugList(
        drugList: List<DrugModel>,
        drugGivenList: List<DrugGivenModel>,
        startDate: Long,
        endDate: Long
    ): List<DrugsGivenAndDrugModel> {
        val result = mutableListOf<DrugsGivenAndDrugModel>()
        drugGivenList.forEach { drugGivenModel ->
            if (drugGivenModel.drugsGivenDate in startDate..endDate) {
                val drug =
                    drugList.find { it.drugCloudDatabaseId == drugGivenModel.drugsGivenDrugId }
                if (drug != null) {
                    val drugsGivenAndDrugModel = drugGivenModel.toDrugGivenAndDrug(drug)
                    result.add(drugsGivenAndDrugModel)
                }
            }
        }
        return result
    }
}