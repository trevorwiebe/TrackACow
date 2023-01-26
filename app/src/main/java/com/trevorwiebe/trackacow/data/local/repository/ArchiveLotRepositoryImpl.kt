package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.cacheDao.CacheArchivedLotDao
import com.trevorwiebe.trackacow.data.local.dao.ArchivedLotDao
import com.trevorwiebe.trackacow.data.mapper.toArchiveLotEntity
import com.trevorwiebe.trackacow.data.mapper.toCacheArchiveLotEntity
import com.trevorwiebe.trackacow.domain.models.archive_lot.ArchiveLotModel
import com.trevorwiebe.trackacow.domain.models.archive_lot.CacheArchiveLotModel
import com.trevorwiebe.trackacow.domain.repository.local.ArchiveLotRepository

class ArchiveLotRepositoryImpl(
    private val archivedLotDao: ArchivedLotDao,
    private val cacheArchivedLotDao: CacheArchivedLotDao
) : ArchiveLotRepository {

    override suspend fun insertArchiveLot(archiveLot: ArchiveLotModel): Long {
        return archivedLotDao.insertArchiveLot(archiveLot.toArchiveLotEntity())
    }

    override suspend fun insertCacheArchiveLot(cacheArchiveLotModel: CacheArchiveLotModel) {
        cacheArchivedLotDao.insertCacheArchiveLot(cacheArchiveLotModel.toCacheArchiveLotEntity())
    }
}