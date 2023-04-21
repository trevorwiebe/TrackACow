package com.trevorwiebe.trackacow.domain.use_cases

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult

class InitiateCloudDatabaseMigration5to6(
        private var firebaseFunctions: FirebaseFunctions
) {

    operator fun invoke(appVersion: Long): Task<HttpsCallableResult> {

        val data = hashMapOf("appVersion" to appVersion)

        return firebaseFunctions
            .getHttpsCallable("migrateDatabase")
            .call(data)
    }
}