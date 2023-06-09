package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility

data class MergeLots(
    private val lotRepository: LotRepository,
    private val lotRepositoryRemote: LotRepositoryRemote,
    private val callRepository: CallRepository,
    private val callRepositoryRemote: CallRepositoryRemote,
    private val cowRepository: CowRepository,
    private val cowRepositoryRemote: CowRepositoryRemote,
    private val drugsGivenRepository: DrugsGivenRepository,
    private val drugsGivenRepositoryRemote: DrugsGivenRepositoryRemote,
    private val feedRepository: FeedRepository,
    private val feedRepositoryRemote: FeedRepositoryRemote,
    private val loadRepository: LoadRepository,
    private val loadRemoteRepository: LoadRemoteRepository,
    private val context: Application
) {

    suspend operator fun invoke(lotModelIdList: List<String>) {

        // TODO: update lots        local-X     remote      cache-
        // TODO: update Calls       local-X     remote-     cache-
        // TODO: update cows        local-X     remote-     cache-
        // TODO: update drugsGiven  local-X     remote-     cache-
        // TODO: update feed        local-X     remote-     cache-
        // TODO: update loads       local-X     remote-     cache-

        val lotToSaveId = lotModelIdList[0]
        val lotsToDeleteIds = lotModelIdList - lotToSaveId

        lotRepository.deleteLotListById(lotsToDeleteIds)
        callRepository.updateCallsWithNewLot(lotToSaveId, lotsToDeleteIds)
        cowRepository.updateCowsWithNewLot(lotToSaveId, lotsToDeleteIds)
        drugsGivenRepository.updateDrugsGivenWithNewLot(lotToSaveId, lotsToDeleteIds)
        feedRepository.updateFeedsWithNewLot(lotToSaveId, lotsToDeleteIds)
        loadRepository.updateLoadWithNewLot(lotToSaveId, lotsToDeleteIds)

        if (Utility.haveNetworkConnection(context)) {
        } else {
            Utility.setNewDataToUpload(context, true)
        }
    }
}