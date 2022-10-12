package com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import com.trevorwiebe.trackacow.domain.repository.remote.DrugRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class CreateDrug(
    private val drugRepository: DrugRepository,
    private val drugRepositoryRemote: DrugRepositoryRemote,
    private val context: Application
) {
    suspend operator fun invoke(drugModel: DrugModel){

        val localDbId = drugRepository.insertDrug(drugModel)

        drugModel.primaryKey = localDbId.toInt()

        if(Utility.haveNetworkConnection(context)){
            drugRepositoryRemote.insertDrug(drugModel)
        }else{
            drugRepository.insertCacheDrug(drugModel.toCacheDrugModel(Constants.INSERT_UPDATE))
        }

    }
}