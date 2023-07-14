package com.trevorwiebe.trackacow.presentation.main_activity

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trevorwiebe.trackacow.BuildConfig
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.fragment_feed.FeedContainerFragment
import com.trevorwiebe.trackacow.presentation.fragment_medicate.MedicateFragment
import com.trevorwiebe.trackacow.presentation.fragment_more.MoreFragment
import com.trevorwiebe.trackacow.presentation.fragment_move.MoveFragment
import com.trevorwiebe.trackacow.presentation.fragment_report.ReportsFragment
import com.trevorwiebe.trackacow.presentation.sign_in.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private var mAuthListener: AuthStateListener? = null
    private var mFirebaseAuth = FirebaseAuth.getInstance()
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

        if (BuildConfig.DEBUG) {
            try {
                mFirebaseAuth.useEmulator("10.0.2.2", 9099)
                val firebaseDatabase = FirebaseDatabase.getInstance()
                firebaseDatabase.useEmulator("10.0.2.2", 9000)
            } catch (e: Exception) {
                Log.e("MainActivity", "onCreate: Cannot start emulator", e)
            }
        }

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

        val migrationAlertDialog = MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle("Please wait")
            .setMessage("Updating cloud database to new version")
            .setCancelable(false)
            .create()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mainViewModel.uiState.collect {
                    if (it.cloudDatabaseMigrationInProgress) {
                        migrationAlertDialog.show()
                    } else {
                        migrationAlertDialog.hide()
                    }
                }
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
        val uploadMenu = menu.findItem(R.id.action_upload_data)
        uploadMenu.isVisible =
                Utility.isThereNewDataToUpload(this@MainActivity)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_upload_data) {
            val dataToUploadDialog = AlertDialog.Builder(this@MainActivity)
            dataToUploadDialog.setTitle("Local changes made")
            dataToUploadDialog.setMessage("You have made local changes that are not synced to the cloud.")
            dataToUploadDialog.setNegativeButton("Cancel") { _: DialogInterface?, _: Int -> }
            dataToUploadDialog.setPositiveButton("Sync") { _: DialogInterface?, _: Int ->
                Toast.makeText(
                        this@MainActivity,
                        "Not implemented yet",
                        Toast.LENGTH_SHORT
                ).show()
            }
            dataToUploadDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onSignedInInitialized() {

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

        // Check data cache and upload if network is available and data is present
        mainViewModel.onEvent(MainUiEvent.CheckCache)

        // Check if app is on required version
        checkIfAppIsOnRequiredVersion()
    }

    private fun onSignedOutCleanUp() {
        Utility.saveLastUsedScreen(this@MainActivity, Constants.MEDICATE)
        mainViewModel.onEvent(MainUiEvent.OnSignedOut)
    }

    private fun checkIfAppIsOnRequiredVersion() {
        val appVersionRef = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("requiredAppVersion")
        var appVersion: Long
        try {
            val pInfo = this@MainActivity.packageManager.getPackageInfo(this@MainActivity.packageName, 0)
            appVersion = pInfo.versionCode.toLong()
        } catch (e: PackageManager.NameNotFoundException) {
            appVersion = 0
            e.printStackTrace()
        }

        val appVersionCode = appVersion
        appVersionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    val databaseAppVersion = snapshot.value as Long
                    if (databaseAppVersion > appVersion) {
                        showNoPassDialog(
                                "Update required",
                                "You must update to the latest version of TrackACow to continue.",
                                "Update",
                                "Cancel",
                                "market://details?id=$packageName",
                                "https://play.google.com/store/apps/details?id=$packageName"
                        )
                    } else if (databaseAppVersion < appVersion) {
                        mainViewModel.onEvent(MainUiEvent.OnInitiateCloudDatabaseMigration(appVersionCode))
                    }
                } else {
                    // Since there is no version save in cloud,
                    // just set cloud to current version
                    appVersionRef.setValue(appVersion)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showNoPassDialog(title: String, message: String, posBtn: String, negBtn: String, firstUrl: String, secondUrl: String) {

        mBottomNavigationView.visibility = View.GONE
        mToolBar.visibility = View.GONE
        mMainLayout.visibility = View.GONE

        val noPassDialog = AlertDialog.Builder(this@MainActivity)
        noPassDialog.setTitle(title)
        noPassDialog.setMessage(message)
        noPassDialog.setCancelable(false)
        noPassDialog.setPositiveButton(posBtn) { _: DialogInterface?, _: Int ->
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(firstUrl))
                startActivity(browserIntent)
            } catch (e: Exception) {
                val secondIntent = Intent(Intent.ACTION_VIEW, Uri.parse(secondUrl))
                startActivity(secondIntent)
            }
        }
        noPassDialog.setNegativeButton(negBtn) { _: DialogInterface?, _: Int -> finish() }
        noPassDialog.show()
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