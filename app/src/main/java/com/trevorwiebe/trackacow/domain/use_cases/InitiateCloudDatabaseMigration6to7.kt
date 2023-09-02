package com.trevorwiebe.trackacow.domain.use_cases

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult

class InitiateCloudDatabaseMigration6to7(
    private var firebaseFunctions: FirebaseFunctions
) {

    operator fun invoke(databaseVersion: Long): Task<HttpsCallableResult> {

        val data = hashMapOf("databaseVersion" to databaseVersion)

        return firebaseFunctions
            .getHttpsCallable("migrateDatabase6to7")
            .call(data)
    }
}