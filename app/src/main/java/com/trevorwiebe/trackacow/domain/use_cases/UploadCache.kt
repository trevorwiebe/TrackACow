package com.trevorwiebe.trackacow.domain.use_cases

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
) {
    suspend operator fun invoke() {

        val cacheCalls = callRepository.getCacheCalls()
        callRepositoryRemote.updateRemoteWithCacheCallList(cacheCalls)

        val cacheCows = cowRepository.getCacheCows()
        if (cacheCows.isNotEmpty())
            cowRepositoryRemote.insertCacheCowsRemote(cacheCows)

        val cacheDrugs = drugRepository.getCacheDrugs()
        if (cacheDrugs.isNotEmpty())
            drugRepositoryRemote.insertCacheDrugs(cacheDrugs)

        val cacheDrugsGiven = drugsGivenRepository.getCacheDrugsGiven()
        if (cacheDrugsGiven.isNotEmpty())
            drugsGivenRepositoryRemote.insertCacheDrugsGiven(cacheDrugsGiven)

        val cacheFeeds = feedRepository.getCacheFeeds()
        if (cacheFeeds.isNotEmpty())
            feedRepositoryRemote.insertCacheFeeds(cacheFeeds)

        val cacheLoads = loadRepository.getCacheLoads()
        if (cacheLoads.isNotEmpty())
            loadRemoteRepository.insertCacheLoadsRemote(cacheLoads)

        val cacheLots = lotRepository.getCacheLots()
        if (cacheLots.isNotEmpty())
            lotRepositoryRemote.insertCacheLotRemote(cacheLots)

        val cachePens = penRepository.getCachePens()
        if (cachePens.isNotEmpty())
            penRepositoryRemote.insertCachePenRemote(cachePens)

        val cacheRations = rationRepository.getCacheRations()
        if (cacheRations.isNotEmpty())
            rationRepositoryRemote.insertCacheRationRemote(cacheRations)

    }
}