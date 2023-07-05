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

    suspend operator fun invoke() {
        callRepository.deleteAllCalls()
        callRepository.deleteCacheCalls()
        cowRepository.deleteAllCows()
        cowRepository.deleteCacheCows()
        drugRepository.deleteAllDrugs()
        drugRepository.deleteCacheDrugs()
        drugsGivenRepository.deleteAllDrugsGiven()
        drugsGivenRepository.deleteCacheDrugsGiven()
        feedRepository.deleteAllFeeds()
        feedRepository.deleteCacheFeeds()
        loadRepository.deleteAllLoads()
        loadRepository.deleteCacheLoads()
        lotRepository.deleteAllLots()
        lotRepository.deleteCacheLots()
        penRepository.deleteAllPens()
        penRepository.deleteCachePens()
        rationRepository.deleteAllRations()
        rationRepository.deleteCacheRations()
    }
}