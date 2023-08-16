package com.trevorwiebe.trackacow.presentation.fragment_settings

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.domain.utils.Utility.haveNetworkConnection
import com.trevorwiebe.trackacow.domain.utils.Utility.isThereNewDataToUpload
import com.trevorwiebe.trackacow.domain.utils.Utility.setLastSync
import com.trevorwiebe.trackacow.domain.utils.Utility.setNewDataToUpload
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mContext: Context

    private val settingsFragmentViewModel: SettingsFragmentViewModel by viewModels()

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val offlineMode = findPreference<CheckBoxPreference>("off_line_mode")
        offlineMode?.setOnPreferenceChangeListener { _, newValue ->
            val offlineSetting: Boolean = newValue as Boolean
            Utility.setOfflineMode(offlineSetting, mContext)
            true
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                settingsFragmentViewModel.uiState.collect {
                    val firebaseUser = it.firebaseUser
                    val accountNamePref = findPreference<Preference>("account_name")
                    if (firebaseUser != null) {
                        val name = firebaseUser.displayName
                        val email = firebaseUser.email
                        val accountName = "$name, $email"
                        if (accountNamePref != null) {
                            accountNamePref.summary = accountName
                        }
                    }
                }
            }
        }

        val signOutPref = findPreference<Preference>("sign_out")
        if (signOutPref != null) {
            signOutPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (isThereNewDataToUpload(mContext) && haveNetworkConnection(mContext)) {
                    val thereIsDataDialog: AlertDialog.Builder = AlertDialog.Builder(mContext)
                    thereIsDataDialog.setPositiveButton("Backup and sign out") { _, _ ->
                        settingsFragmentViewModel.onEvent(SettingsFragmentEvent.OnUploadCache)
                        signOut()
                    }
                    thereIsDataDialog.setNeutralButton("Sign out anyway") { _, _ ->
                        signOut()
                    }
                    thereIsDataDialog.setMessage("There is information that hasn't been saved to the cloud.  Signing out will delete this data.  Backup to cloud to save your data. You must have an internet connection prior to backing up.")
                    thereIsDataDialog.show()
                } else if (isThereNewDataToUpload(mContext)) {
                    val networkRequired: AlertDialog.Builder = AlertDialog.Builder(mContext)
                    networkRequired.setMessage("There is information that hasn't been saved to the cloud.  Signing out will delete this data.  Please connect to the network and backup to cloud to save your data.")
                    networkRequired.setPositiveButton("Cancel") { _, _ ->

                    }
                    networkRequired.setNeutralButton("Sign out anyway") { _, _ ->
                        signOut()
                    }
                    networkRequired.show()
                } else {
                    signOut()
                }
                false
            }
        }

        val fiveStarReview = findPreference<Preference>("five_star_review")
        if (fiveStarReview != null) {
            fiveStarReview.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val packageName = "com.trevorwiebe.trackacow"
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$packageName")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                }
                false
            }
        }

        val versionPref = findPreference<Preference>("version")
        try {
            if (versionPref != null) {
                versionPref.summary = appVersion()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun appVersion(): String {
        val pInfo = mContext.packageManager.getPackageInfo(
            mContext.packageName, 0
        )
        return pInfo.versionName
    }

    private fun signOut() {
        settingsFragmentViewModel.onEvent(SettingsFragmentEvent.OnSignOut)
        setNewDataToUpload((mContext), false)
        setLastSync((mContext), 0)
        if (activity != null) {
            requireActivity().finish()
        }
    }
}