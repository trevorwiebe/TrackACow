package com.trevorwiebe.trackacow.domain.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun <T> DatabaseReference.addSingleValueEventListenerFlow(dataType: Class<T>): Flow<T> =
    callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(dataType)?.let { trySend(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(error.message)
            }
        }
        addValueEventListener(listener)
        awaitClose { removeEventListener(listener) }
    }