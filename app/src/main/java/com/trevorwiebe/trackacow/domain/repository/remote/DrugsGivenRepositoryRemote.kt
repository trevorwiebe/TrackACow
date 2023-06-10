package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel

interface DrugsGivenRepositoryRemote {

    suspend fun createDrugsGivenListRemote(drugsGivenList: List<DrugGivenModel>)

    suspend fun editRemoteDrugsGiven(drugsGivenModel: DrugGivenModel)

    suspend fun updateDrugsGivenWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdListToDelete: List<String>
    )

    suspend fun deleteRemoteDrugsGivenByCowId(cowId: String)

    suspend fun deleteRemoteDrugGivenByDrugGivenId(drugGivenId: String)
}