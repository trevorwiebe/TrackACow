package com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class UpdateDrugGiven(
    private val drugsGivenRepository: DrugsGivenRepository,
    private val drugsGivenRepositoryRemote: DrugsGivenRepositoryRemote,
    private val context: Application
) {
    suspend operator fun invoke(drugGivenModel: DrugGivenModel) {

        drugsGivenRepository.editDrugsGiven(drugGivenModel)

        if (Utility.haveNetworkConnection(context)) {
            drugsGivenRepositoryRemote.editRemoteDrugsGiven(drugGivenModel)
        } else {
            Utility.setNewDataToUpload(context, true)
            drugsGivenRepository.insertCacheDrugGiven(
                drugGivenModel.toCacheDrugGivenModel(Constants.INSERT_UPDATE)
            )
        }
    }
}