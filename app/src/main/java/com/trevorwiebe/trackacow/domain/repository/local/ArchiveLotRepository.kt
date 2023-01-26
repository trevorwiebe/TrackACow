package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.archive_lot.ArchiveLotModel
import com.trevorwiebe.trackacow.domain.models.archive_lot.CacheArchiveLotModel

interface ArchiveLotRepository {

    suspend fun insertArchiveLot(archiveLot: ArchiveLotModel): Long

    suspend fun insertCacheArchiveLot(cacheArchiveLotModel: CacheArchiveLotModel)
}