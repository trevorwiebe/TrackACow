package com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import com.trevorwiebe.trackacow.domain.repository.remote.DrugRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class UpdateDrug(
    private val drugRepository: DrugRepository,
    private val drugRepositoryRemote: DrugRepositoryRemote,
    private val context: Application
) {
   suspend operator fun invoke(drugModel: DrugModel) {

       drugRepository.updateDrug(drugModel)

       if (Utility.haveNetworkConnection(context)) {
           // insertDrug uses the same code as updateDrug would
           drugRepositoryRemote.insertDrug(drugModel)
       } else {
           Utility.setNewDataToUpload(context, true)
           drugRepository.insertCacheDrug(drugModel.toCacheDrugModel(Constants.INSERT_UPDATE))
       }

   }
}