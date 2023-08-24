package com.trevorwiebe.trackacow.domain.use_cases

import com.trevorwiebe.trackacow.data.local.AppDatabase

class DatabaseVersionHelper(
    private var database: AppDatabase
) {

    fun getDbVersion(): Long {
        return database.openHelper.readableDatabase.version.toLong()
    }
}