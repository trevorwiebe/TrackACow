package com.trevorwiebe.trackacow.data.local

import androidx.room.Database
import com.trevorwiebe.trackacow.data.entities.PenEntity
import com.trevorwiebe.trackacow.data.entities.CowEntity
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity
import com.trevorwiebe.trackacow.data.entities.DrugEntity
import com.trevorwiebe.trackacow.data.entities.LotEntity
import com.trevorwiebe.trackacow.data.entities.CallEntity
import com.trevorwiebe.trackacow.data.entities.FeedEntity
import com.trevorwiebe.trackacow.data.entities.UserEntity
import com.trevorwiebe.trackacow.data.entities.LoadEntity
import com.trevorwiebe.trackacow.data.entities.RationEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CachePenEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCowEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheUserEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLoadEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCallEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheFeedEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheRationEntity
import androidx.room.RoomDatabase
import com.trevorwiebe.trackacow.data.local.dao.PenDao
import com.trevorwiebe.trackacow.data.local.dao.CowDao
import com.trevorwiebe.trackacow.data.local.dao.DrugsGivenDao
import com.trevorwiebe.trackacow.data.local.dao.DrugDao
import com.trevorwiebe.trackacow.data.local.dao.LotDao
import com.trevorwiebe.trackacow.data.local.dao.CallDao
import com.trevorwiebe.trackacow.data.local.dao.FeedDao
import com.trevorwiebe.trackacow.data.local.dao.UserDao
import com.trevorwiebe.trackacow.data.local.dao.LoadDao
import com.trevorwiebe.trackacow.data.local.dao.RationDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CachePenDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheCowDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheDrugsGivenDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheDrugDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheLotDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheUserDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheLoadDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheCallDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheFeedDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheRationDao

@Database(
    entities = [
        PenEntity::class,
        CowEntity::class,
        DrugsGivenEntity::class,
        DrugEntity::class,
        LotEntity::class,
        CallEntity::class,
        FeedEntity::class,
        UserEntity::class,
        LoadEntity::class,
        RationEntity::class,
        CachePenEntity::class,
        CacheCowEntity::class,
        CacheDrugsGivenEntity::class,
        CacheDrugEntity::class,
        CacheLotEntity::class,
        CacheUserEntity::class,
        CacheLoadEntity::class,
        CacheCallEntity::class,
        CacheFeedEntity::class,
        CacheRationEntity::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun penDao(): PenDao
    abstract fun cowDao(): CowDao
    abstract fun drugsGivenDao(): DrugsGivenDao
    abstract fun drugDao(): DrugDao
    abstract fun lotDao(): LotDao
    abstract fun callDao(): CallDao
    abstract fun feedDao(): FeedDao
    abstract fun userDao(): UserDao
    abstract fun loadDao(): LoadDao
    abstract fun rationDao(): RationDao
    abstract fun cachePenDao(): CachePenDao
    abstract fun cacheCowDao(): CacheCowDao
    abstract fun cacheDrugsGivenDao(): CacheDrugsGivenDao
    abstract fun cacheDrugDao(): CacheDrugDao
    abstract fun cacheLotDao(): CacheLotDao
    abstract fun cacheUserDao(): CacheUserDao
    abstract fun cacheLoadDao(): CacheLoadDao
    abstract fun cacheCallDao(): CacheCallDao
    abstract fun cacheFeedDao(): CacheFeedDao
    abstract fun cacheRationDao(): CacheRationDao
}