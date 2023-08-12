package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.CacheDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import kotlinx.coroutines.flow.Flow

interface DrugsGivenRepositoryRemote {

    suspend fun createDrugsGivenListRemote(drugsGivenList: List<DrugGivenModel>)

    fun readDrugsGivenAndDrugsByLotIdRemote(lotId: String): Flow<Pair<List<DrugModel>, List<DrugGivenModel>>>

    fun readDrugsGivenAndDrugsByCowIdRemote(cowId: String): Flow<Pair<List<DrugModel>, List<DrugGivenModel>>>

    suspend fun insertCacheDrugsGiven(drugsGivenList: List<CacheDrugGivenModel>)

    suspend fun editRemoteDrugsGiven(drugsGivenModel: DrugGivenModel)

    suspend fun updateDrugsGivenWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdListToDelete: List<String>
    )

    suspend fun deleteRemoteDrugsGivenByCowId(cowId: String)

    suspend fun deleteRemoteDrugGivenByDrugGivenId(drugGivenId: String)
}