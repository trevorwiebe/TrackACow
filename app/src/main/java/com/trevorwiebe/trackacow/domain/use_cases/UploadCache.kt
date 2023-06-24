package com.trevorwiebe.trackacow.domain.use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.DrugRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.RationRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility

class UploadCache(
    private val callRepository: CallRepository,
    private val callRepositoryRemote: CallRepositoryRemote,
    private val cowRepository: CowRepository,
    private val cowRepositoryRemote: CowRepositoryRemote,
    private val drugRepository: DrugRepository,
    private val drugRepositoryRemote: DrugRepositoryRemote,
    private val drugsGivenRepository: DrugsGivenRepository,
    private val drugsGivenRepositoryRemote: DrugsGivenRepositoryRemote,
    private val feedRepository: FeedRepository,
    private val feedRepositoryRemote: FeedRepositoryRemote,
    private val loadRepository: LoadRepository,
    private val loadRemoteRepository: LoadRemoteRepository,
    private val lotRepository: LotRepository,
    private val lotRepositoryRemote: LotRepositoryRemote,
    private val penRepository: PenRepository,
    private val penRepositoryRemote: PenRepositoryRemote,
    private val rationRepository: RationsRepository,
    private val rationRepositoryRemote: RationRepositoryRemote,
    private val context: Application
) {
    suspend operator fun invoke() {

        val cacheCalls = callRepository.getCacheCalls()
        callRepositoryRemote.updateRemoteWithCacheCallList(cacheCalls)
        callRepository.deleteCacheCalls()

        val cacheCows = cowRepository.getCacheCows()
        cowRepositoryRemote.insertCacheCowsRemote(cacheCows)
        cowRepository.deleteCacheCows()

        val cacheDrugs = drugRepository.getCacheDrugs()
        drugRepositoryRemote.insertCacheDrugs(cacheDrugs)
        drugRepository.getCacheDrugs()

        val cacheDrugsGiven = drugsGivenRepository.getCacheDrugsGiven()
        drugsGivenRepositoryRemote.insertCacheDrugsGiven(cacheDrugsGiven)
        drugsGivenRepository.deleteCacheDrugsGiven()

        val cacheFeeds = feedRepository.getCacheFeeds()
        feedRepositoryRemote.insertCacheFeeds(cacheFeeds)
        feedRepository.deleteCacheFeeds()

        val cacheLoads = loadRepository.getCacheLoads()
        loadRemoteRepository.insertCacheLoadsRemote(cacheLoads)
        loadRepository.deleteCacheLoads()

        val cacheLots = lotRepository.getCacheLots()
        lotRepositoryRemote.insertCacheLotRemote(cacheLots)
        lotRepository.deleteCacheLots()

        val cachePens = penRepository.getCachePens()
        penRepositoryRemote.insertCachePenRemote(cachePens)
        penRepository.deleteCachePens()

        val cacheRations = rationRepository.getCacheRations()
        rationRepositoryRemote.insertCacheRationRemote(cacheRations)
        rationRepository.deleteCacheRations()

        Utility.setNewDataToUpload(context, false)
    }
}