package com.trevorwiebe.trackacow.domain.use_cases

import com.google.firebase.functions.FirebaseFunctions

class InitiateCloudDatabaseMigration5to6(
        private var firebaseFunctions: FirebaseFunctions
) {

    operator fun invoke(appVersion: Long): String {

        val data = hashMapOf("appVersion" to appVersion)

        firebaseFunctions
                .getHttpsCallable("migrateCloud")
                .call(data)

        return ""
    }
}