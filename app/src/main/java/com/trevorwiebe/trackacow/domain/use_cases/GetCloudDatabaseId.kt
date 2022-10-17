package com.trevorwiebe.trackacow.domain.use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.utils.Constants

class GetCloudDatabaseId(
    val databaseReference: FirebaseDatabase
) {
    operator fun invoke(stringIfNull: String): String{
        return databaseReference
            .getReference(Constants.BASE_REFERENCE_STRING)
            .push()
            .key ?: stringIfNull
    }
}