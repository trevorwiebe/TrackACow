package com.trevorwiebe.trackacow.test_utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.data.local.AppDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class TrackACowAndroidTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    protected lateinit var context: Context

    private lateinit var authSignal: CountDownLatch

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var firebaseDatabase: FirebaseDatabase

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Before
    open fun setUp() {
        authSignal = CountDownLatch(1)
        context = ApplicationProvider.getApplicationContext()
        hiltRule.inject()
        if (firebaseAuth.currentUser == null) {
            firebaseAuth.signInWithEmailAndPassword("test@example.com", "testing")
                .addOnCompleteListener {
                    authSignal.countDown()
                }
        } else {
            authSignal.countDown()
        }
        authSignal.await(10, TimeUnit.SECONDS)
    }

    @After
    open fun tearDown() {
        db.clearAllTables()
        firebaseDatabase.getReference("/").removeValue()
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()
        }
    }
}