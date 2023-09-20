package com.trevorwiebe.trackacow.test_utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.trevorwiebe.trackacow.data.local.AppDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

abstract class TrackACowAndroidTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    protected lateinit var context: Context

    @Inject
    lateinit var db: AppDatabase

    @Before
    open fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        hiltRule.inject()
    }

    @After
    open fun tearDown() {
        db.clearAllTables()
    }
}