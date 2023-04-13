package com.trevorwiebe.trackacow.domain.use_cases

import com.trevorwiebe.trackacow.domain.repository.local.*

class DeleteAllLocalData(
        private val callRepository: CallRepository,
        private val cowRepository: CowRepository,
        private val drugRepository: DrugRepository,
        private val drugsGivenRepository: DrugsGivenRepository,
        private val feedRepository: FeedRepository,
        private val loadRepository: LoadRepository,
        private val lotRepository: LotRepository,
        private val penRepository: PenRepository,
        private val rationRepository: RationsRepository
) {

    // TODO: add code to delete cache databases too

    suspend operator fun invoke() {
        callRepository.deleteAllCalls()
        cowRepository.deleteAllCows()
        drugRepository.deleteAllDrugs()
        drugsGivenRepository.deleteAllDrugsGiven()
        feedRepository.deleteAllFeeds()
        loadRepository.deleteAllLoads()
        lotRepository.deleteAllLots()
        penRepository.deleteAllPens()
        rationRepository.deleteAllRations()
    }
}