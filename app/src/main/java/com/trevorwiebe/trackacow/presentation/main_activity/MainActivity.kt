package com.trevorwiebe.trackacow.presentation.main_activity

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.work.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.data.entities.UserEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.main.user.QueryUserEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.main.user.QueryUserEntity.OnUserLoaded
import com.trevorwiebe.trackacow.domain.dataLoaders.misc.DeleteAllLocalData
import com.trevorwiebe.trackacow.domain.services.SyncDatabaseService
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.SyncDatabase
import com.trevorwiebe.trackacow.domain.utils.SyncDatabase.OnDatabaseSynced
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.fragment_feed.FeedContainerFragment
import com.trevorwiebe.trackacow.presentation.fragment_medicate.MedicateFragment
import com.trevorwiebe.trackacow.presentation.fragment_more.MoreFragment
import com.trevorwiebe.trackacow.presentation.fragment_move.MoveFragment
import com.trevorwiebe.trackacow.presentation.fragment_report.ReportsFragment
import com.trevorwiebe.trackacow.presentation.sign_in.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnDatabaseSynced, OnUserLoaded {

    private var mAuthListener: AuthStateListener? = null
    private var mFirebaseAuth = FirebaseAuth.getInstance()
    private val numberFormat = NumberFormat.getInstance(Locale.getDefault())
    private var mSyncDatabase: SyncDatabase? = null
    private var mIsActivityPaused = true

    private lateinit var mBottomNavigationView: BottomNavigationView
    private lateinit var mMainProgressBar: ProgressBar
    private lateinit var mToolBar: Toolbar
    private lateinit var mMainLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mToolBar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolBar)
        mMainLayout = findViewById(R.id.main_fragment_container)
        mMainProgressBar = findViewById(R.id.main_progress_bar)
        mBottomNavigationView = findViewById(R.id.bottom_navigation)
        mBottomNavigationView.visibility = View.INVISIBLE
        mBottomNavigationView.setOnItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem: MenuItem ->
            mBottomNavigationView.menu.setGroupCheckable(0, true, true)
            val id = menuItem.itemId
            setSelectedFragment(Utility.getFragmentIdFromResourceID(id))
            true
        })

        mAuthListener = AuthStateListener { firebaseAuth: FirebaseAuth ->
            mFirebaseAuth = firebaseAuth
            val user = firebaseAuth.currentUser
            if (user == null) {
                onSignedOutCleanUp()
                val signInIntent = Intent(this@MainActivity, SignInActivity::class.java)
                startActivity(signInIntent)
            } else {
                onSignedInInitialized()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
        mFirebaseAuth.addAuthStateListener(mAuthListener!!)
        mIsActivityPaused = false
    }

    override fun onPause() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener!!)
        mIsActivityPaused = true
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        val upload_menu = menu.findItem(R.id.action_upload_data)
        upload_menu.isVisible =
            Utility.isThereNewDataToUpload(this@MainActivity)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_sync_data) {
            mMainProgressBar.visibility = View.VISIBLE
            mSyncDatabase = SyncDatabase(this@MainActivity, this@MainActivity)
            mSyncDatabase!!.beginSync()
        } else {
            val dataToUploadDialog = AlertDialog.Builder(this@MainActivity)
            dataToUploadDialog.setTitle("Local changes made")
            dataToUploadDialog.setMessage("You have made local changes that are not synced to the cloud.")
            dataToUploadDialog.setNegativeButton("Cancel") { _: DialogInterface?, _: Int -> }
            dataToUploadDialog.setPositiveButton("Sync") { _: DialogInterface?, _: Int ->
                mMainProgressBar.visibility = View.VISIBLE
                mSyncDatabase = SyncDatabase(this@MainActivity, this@MainActivity)
                mSyncDatabase!!.beginSync()
            }
            dataToUploadDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onSignedInInitialized() {

        val currentTime = System.currentTimeMillis()
        val lastSyncTime = Utility.getLastSync(this@MainActivity)
        val timeElapsed = currentTime - lastSyncTime
        val timeUntilNextLoad = TimeUnit.MINUTES.toMillis(30)
        if (timeElapsed >= timeUntilNextLoad) {
            mMainProgressBar.visibility = View.VISIBLE
            mSyncDatabase = SyncDatabase(this@MainActivity, this@MainActivity)
            mSyncDatabase!!.beginSync()
        }
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresDeviceIdle(true)
            .build()
        val request: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<SyncDatabaseService>(24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "sync_database_job_tag",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        val lastUsedScreenFragId = Utility.getLastUsedScreen(this@MainActivity)
        mBottomNavigationView.visibility = View.VISIBLE
        mBottomNavigationView.selectedItemId =
            Utility.getResourceIdFromFragmentId(
                lastUsedScreenFragId
            )
        setSelectedFragment(lastUsedScreenFragId)
        mBottomNavigationView.menu.setGroupCheckable(
            0,
            lastUsedScreenFragId != Constants.MOVE,
            true
        )
    }

    private fun onSignedOutCleanUp() {
        Utility.saveLastUsedScreen(this@MainActivity, Constants.MEDICATE)
        DeleteAllLocalData().execute(this@MainActivity)
    }

    override fun onDatabaseSynced(resultCode: Int) {
        when (resultCode) {
            Constants.SUCCESS -> {
                val uid = mFirebaseAuth.currentUser!!.uid
                QueryUserEntity(uid, this@MainActivity).execute(this@MainActivity)
                Utility.setNewDataToUpload(this@MainActivity, false)
                invalidateOptionsMenu()
                setSelectedFragment(Utility.getLastUsedScreen(this@MainActivity))
            }
            Constants.ERROR_FETCHING_DATA_FROM_CLOUD -> Toast.makeText(
                this,
                "There was an error fetching the data from the cloud.",
                Toast.LENGTH_SHORT
            ).show()
            Constants.ERROR_PUSHING_DATA_TO_CLOUD -> Toast.makeText(
                this,
                "There was an error pushing data to the cloud.",
                Toast.LENGTH_SHORT
            ).show()
            Constants.NO_NETWORK_CONNECTION -> Toast.makeText(
                this,
                "No internet connection.",
                Toast.LENGTH_SHORT
            ).show()
            Constants.ERROR_ACTIVITY_DESTROYED_BEFORE_LOADED -> {}
            else -> Toast.makeText(
                this,
                "An unknown error occurred while syncing database",
                Toast.LENGTH_SHORT
            ).show()
        }
        mMainProgressBar.visibility = View.INVISIBLE
    }

    override fun onUserLoaded(userEntity: UserEntity?) {
        if (userEntity == null) return
        val currentTime = System.currentTimeMillis()
        val renewalDate = userEntity.renewalDate
        val timeElapsed = renewalDate - currentTime
        val daysLeft = (timeElapsed / (1000 * 60 * 60 * 24)).toInt().toLong()
        val daysLeftStr = numberFormat.format(daysLeft)
        val title: String
        var message =
            "You will need to subscribe to a plan to continue.  All you data is saved and waiting for you."
        when (userEntity.accountType) {
            Constants.FREE_TRIAL -> {
                title = "Your free trial has ended."
                if (daysLeft <= 0) {
                    showNoPassDialog(title, message)
                    Utility.setLastSync(this, System.currentTimeMillis())
                } else if (daysLeft <= 7) {
                    showPassableDialog(
                        "Free trial ends soon.",
                        "You have $daysLeftStr day(s) left on your free trial.  Please subscribe to avoid a stall in service."
                    )
                    Utility.setLastSync(this, System.currentTimeMillis())
                } else {
                    Utility.setLastSync(this, System.currentTimeMillis())
                }
            }
            Constants.MONTHLY_SUBSCRIPTION -> {
                title = "Your monthly subscription has ended."
                if (daysLeft <= -3) {
                    showNoPassDialog(title, message)
                    Utility.setLastSync(this, System.currentTimeMillis())
                } else if (daysLeft <= 0) {
                    val daysLeftOnGracePeriod = daysLeft + 3
                    val daysLeftOnGracePeriodStr = numberFormat.format(daysLeftOnGracePeriod)
                    showPassableDialog(
                        "Monthly subscription has ended.",
                        "But we will give you a 3 day grace period to get your account issues ironed out. There is/are $daysLeftOnGracePeriodStr day(s) left on the grace period."
                    )
                    Utility.setLastSync(this, System.currentTimeMillis())
                } else {
                    Utility.setLastSync(this, System.currentTimeMillis())
                }
            }
            Constants.ANNUAL_SUBSCRIPTION -> {
                title = "Your annual subscription has ended."
                if (daysLeft <= -3) {
                    showNoPassDialog(title, message)
                    Utility.setLastSync(this, System.currentTimeMillis())
                } else if (daysLeft <= 0) {
                    val daysLeftOnGracePeriod = daysLeft + 3
                    val daysLeftOnGracePeriodStr = numberFormat.format(daysLeftOnGracePeriod)
                    showPassableDialog(
                        "Monthly subscription has ended.",
                        "But we will give you a 3 day grace period to get your account issues ironed out. There is/are $daysLeftOnGracePeriodStr day(s) left on the grace period."
                    )
                    Utility.setLastSync(this, System.currentTimeMillis())
                } else {
                    Utility.setLastSync(this, System.currentTimeMillis())
                }
            }
            Constants.CANCELED -> {
                title = "Your account has been canceled."
                message =
                    "You will need to re-subscribe to a plan to continue. Your data may be all saved yet."
                showNoPassDialog(title, message)
                Utility.setLastSync(this, System.currentTimeMillis())
                Utility.setLastSync(this, System.currentTimeMillis())
            }
            Constants.FOREVER_FREE_USER -> Utility.setLastSync(this, System.currentTimeMillis())
            else -> {
                title = "Error"
                message =
                    "Unknown error occurred.  Please send an email to app@trackacow.net for support."
                showNoPassDialog(title, message)
                Utility.setLastSync(this, System.currentTimeMillis())
                return
            }
        }
        mBottomNavigationView.visibility = View.VISIBLE
        mToolBar.visibility = View.VISIBLE
        mMainLayout.visibility = View.VISIBLE
    }

    private fun showPassableDialog(title: String, message: String) {
        val accountEnding = AlertDialog.Builder(this@MainActivity)
        accountEnding.setTitle(title)
        accountEnding.setMessage(message)
        accountEnding.setNegativeButton("Cancel") { _: DialogInterface?, which: Int ->
            Utility.setShouldShowTrialEndsSoon(
                this@MainActivity,
                true
            )
        }
        accountEnding.setPositiveButton("Renew") { _: DialogInterface?, which: Int -> }
        accountEnding.setNeutralButton("Don't show again") { _: DialogInterface?, which: Int ->
            Utility.setShouldShowTrialEndsSoon(
                this@MainActivity,
                false
            )
        }
        if (Utility.shouldShowTrialEndsSoon(this@MainActivity)) {
            accountEnding.show()
        }
    }

    private fun showNoPassDialog(title: String, message: String) {
        val accountEnded = AlertDialog.Builder(this@MainActivity)
        mBottomNavigationView.visibility = View.GONE
        mToolBar.visibility = View.GONE
        mMainLayout.visibility = View.GONE
        accountEnded.setTitle(title)
        accountEnded.setMessage(message)
        accountEnded.setCancelable(false)
        accountEnded.setPositiveButton("Renew") { _: DialogInterface?, _: Int ->
            val browserIntent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    resources.getString(R.string.manage_sub_url)
                )
            )
            startActivity(browserIntent)
        }
        accountEnded.setNegativeButton("Cancel") { _: DialogInterface?, _: Int -> finish() }
        accountEnded.show()
    }

    private fun setSelectedFragment(fragmentId: Int) {
        // save the fragment id
        Utility.saveLastUsedScreen(this, fragmentId)

        // check if the activity has been paused
        if (mIsActivityPaused) return
        when (fragmentId) {
            Constants.FEED -> {
                title = getString(R.string.feed)
                val feedContainerFragment = FeedContainerFragment()
                val feedTransactionManager = supportFragmentManager.beginTransaction()
                feedTransactionManager.replace(R.id.main_fragment_container, feedContainerFragment)
                feedTransactionManager.commit()
            }
            Constants.REPORTS -> {
                title = getString(R.string.reports)
                val reportsFragment = ReportsFragment()
                val reportsTransactionManager = supportFragmentManager.beginTransaction()
                reportsTransactionManager.replace(R.id.main_fragment_container, reportsFragment)
                reportsTransactionManager.commit()
            }
            Constants.MORE -> {
                title = getString(R.string.more)
                val moreFragment = MoreFragment()
                val moreTransactionManager = supportFragmentManager.beginTransaction()
                moreTransactionManager.replace(R.id.main_fragment_container, moreFragment)
                moreTransactionManager.commit()
            }
            Constants.MOVE -> {
                title = getString(R.string.move)
                val moveFragment = MoveFragment()
                val moveTransactionManager = supportFragmentManager.beginTransaction()
                moveTransactionManager.replace(R.id.main_fragment_container, moveFragment)
                moveTransactionManager.commit()
            }
            else -> {
                title = getString(R.string.work)
                val medicateFragmentDefault = MedicateFragment()
                val medicateTransactionManagerDefault = supportFragmentManager.beginTransaction()
                medicateTransactionManagerDefault.replace(
                    R.id.main_fragment_container,
                    medicateFragmentDefault
                )
                medicateTransactionManagerDefault.commit()
            }
        }
    }
}