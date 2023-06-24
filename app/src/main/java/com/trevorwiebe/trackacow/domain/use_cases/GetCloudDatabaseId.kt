package com.trevorwiebe.trackacow.domain.use_cases

import com.google.firebase.database.FirebaseDatabase
import javax.inject.Provider

class GetCloudDatabaseId(
    private val databaseReference: FirebaseDatabase,
    private val firebaseUserId: Provider<String>
) {
    operator fun invoke(stringIfNull: String): String {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            databaseReference
                .getReference("users/${userId}")
                .push()
                .key ?: stringIfNull
        } else {
            stringIfNull
        }
    }
}