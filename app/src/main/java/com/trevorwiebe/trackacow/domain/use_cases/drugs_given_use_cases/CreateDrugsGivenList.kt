package com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class CreateDrugsGivenList(
    private val drugsGivenRepository: DrugsGivenRepository,
    private val drugsGivenRepositoryRemote: DrugsGivenRepositoryRemote,
    private val getCloudDatabaseId: GetCloudDatabaseId,
    private val context: Application
) {
    suspend operator fun invoke(drugList: List<DrugGivenModel>) {

        drugList.forEach {
            it.drugsGivenId = getCloudDatabaseId.invoke("")
        }

        drugsGivenRepository.createDrugsGivenList(drugList)

        if (Utility.haveNetworkConnection(context)) {
            drugsGivenRepositoryRemote.createDrugsGivenListRemote(drugList)
        } else {
            Utility.setNewDataToUpload(context, true)
            drugsGivenRepository.createCacheDrugsGivenList(
                drugList.map {
                    it.toCacheDrugGivenModel(Constants.INSERT_UPDATE)
                }
            )
        }

    }
}