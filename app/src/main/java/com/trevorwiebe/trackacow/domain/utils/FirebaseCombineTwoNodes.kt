package com.trevorwiebe.trackacow.domain.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun <T, U> combineDatabaseNodes(
    reference1: DatabaseReference,
    reference2: DatabaseReference,
    dataType1: Class<T>,
    dataType2: Class<U>
): Flow<Pair<List<T>, List<U>>> = callbackFlow {

    val list1 = mutableListOf<T>()
    val list2 = mutableListOf<U>()

    var value1Returned = false
    var value2Returned = false

    val listener1 = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            list1.clear()
            dataSnapshot.children.forEach {
                val object1 = it.getValue(dataType1)
                if (object1 != null) {
                    list1.add(object1)
                }
            }
            value1Returned = true
            if (value2Returned) {
                trySend(Pair(list1, list2))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            cancel(error.message)
        }
    }
    reference1.addValueEventListener(listener1)

    val listener2 = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            list2.clear()
            dataSnapshot.children.forEach {
                val object2 = it.getValue(dataType2)
                if (object2 != null) {
                    list2.add(object2)
                }
            }
            value2Returned = true
            if (value1Returned) {
                trySend(Pair(list1, list2))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            cancel(error.message)
        }
    }
    reference2.addValueEventListener(listener2)

    awaitClose {
        reference1.removeEventListener(listener1)
        reference2.removeEventListener(listener2)
        value1Returned = false
        value2Returned = false
    }
}

fun <T, U> combineQueryDatabaseNodes(
    reference1: DatabaseReference,
    reference2: Query,
    dataType1: Class<T>,
    dataType2: Class<U>
): Flow<Pair<List<T>, List<U>>> = callbackFlow {

    val list1 = mutableListOf<T>()
    val list2 = mutableListOf<U>()

    var value1Returned = false
    var value2Returned = false

    val listener1 = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            list1.clear()
            dataSnapshot.children.forEach {
                val object1 = it.getValue(dataType1)
                if (object1 != null) {
                    list1.add(object1)
                }
            }
            value1Returned = true
            if (value1Returned && value2Returned) {
                trySend(Pair(list1, list2))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            cancel(error.message)
        }
    }
    reference1.addValueEventListener(listener1)

    val listener2 = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            list2.clear()
            dataSnapshot.children.forEach {
                val object2 = it.getValue(dataType2)
                if (object2 != null) {
                    list2.add(object2)
                }
            }
            value2Returned = true
            if (value1Returned && value2Returned) {
                trySend(Pair(list1, list2))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            cancel(error.message)
        }
    }
    reference2.addValueEventListener(listener2)

    awaitClose {
        reference1.removeEventListener(listener1)
        reference2.removeEventListener(listener2)
        value1Returned = false
        value2Returned = false
    }
}