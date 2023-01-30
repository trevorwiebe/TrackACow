package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel

interface DrugsGivenRepositoryRemote {

    suspend fun editRemoteDrugsGiven(drugsGivenModel: DrugGivenModel)

    suspend fun deleteRemoteDrugsGivenByCowId(cowId: String)

    suspend fun deleteRemoteDrugGivenByDrugGivenId(drugGivenId: String)
}