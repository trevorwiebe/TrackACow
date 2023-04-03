package com.trevorwiebe.trackacow.presentation.fragment_settings

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.dataLoaders.misc.DeleteAllLocalData
import com.trevorwiebe.trackacow.domain.dataLoaders.misc.InsertAllLocalChangeToCloud
import com.trevorwiebe.trackacow.domain.dataLoaders.misc.InsertAllLocalChangeToCloud.OnAllLocalDbInsertedToCloud
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility.isThereNewDataToUpload
import com.trevorwiebe.trackacow.domain.utils.Utility.setLastSync
import com.trevorwiebe.trackacow.domain.utils.Utility.setNewDataToUpload

class SettingsFragment : PreferenceFragmentCompat(), OnAllLocalDbInsertedToCloud {

    private lateinit var mContext: Context
    private var mBackingUpData: AlertDialog.Builder? = null

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val accountNamePref = findPreference<Preference>("account_name")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val name = user.displayName
            val email = user.email
            val accountName = "$name, $email"
            if (accountNamePref != null) {
                accountNamePref.summary = accountName
            }
        }

        val signOutPref = findPreference<Preference>("sign_out")
        if (signOutPref != null) {
            signOutPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (isThereNewDataToUpload(mContext)) {
                    val thereIsDataDialog: AlertDialog.Builder = AlertDialog.Builder(
                        context
                    )
                    thereIsDataDialog.setPositiveButton(
                        "Backup and sign out"
                    ) { _, _ ->
                        setNewDataToUpload(requireContext(), false)
                        mBackingUpData = AlertDialog.Builder(context)
                        val dialogLoadingLayout: View = LayoutInflater.from(context)
                            .inflate(R.layout.dialog_inserting_data_to_cloud, null)
                        mBackingUpData!!.setView(dialogLoadingLayout)
                        mBackingUpData!!.show()
                        InsertAllLocalChangeToCloud(this@SettingsFragment).execute(
                            context
                        )
                    }
                    thereIsDataDialog.setNeutralButton(
                        "Sign out anyway"
                    ) { _, _ -> signOut() }
                    thereIsDataDialog.setTitle("Information will be lost")
                    thereIsDataDialog.setMessage("There is information that hasn't been saved to the cloud.  Signing out will delete this data.  Backup to cloud to save you data. You must have an internet connection prior to backing up.")
                    thereIsDataDialog.show()
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

    override fun onAllLocalDbInsertedToCloud(resultCode: Int) {
        if (resultCode == Constants.SUCCESS) {
            if (mBackingUpData != null) {
                val createDialog = mBackingUpData!!.create()
                createDialog.dismiss()
            }
            signOut()
        } else {
            val message: String = when (resultCode) {
                Constants.NO_NETWORK_CONNECTION -> "You do not have a connection, please connect so you can push your data to the cloud."
                Constants.ERROR_PUSHING_DATA_TO_CLOUD -> "There was an error pushing your data to the cloud."
                else -> "An unknown error occurred while pushing your data to the cloud."
            }
            val errorBackingUpDialog = AlertDialog.Builder(mContext)
            errorBackingUpDialog.setTitle("There was an error backing up your data.")
            errorBackingUpDialog.setMessage(message)
            errorBackingUpDialog.setPositiveButton(
                "Try again"
            ) { _, _ ->
                InsertAllLocalChangeToCloud(this@SettingsFragment).execute(
                    context
                )
            }
            errorBackingUpDialog.setNegativeButton(
                "Cancel"
            ) { _, _ -> }
            errorBackingUpDialog.setNeutralButton(
                "Sign out anyway"
            ) { _, _ -> signOut() }
            errorBackingUpDialog.show()
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        if (activity != null) {
            requireActivity().finish()
        }
        DeleteAllLocalData().execute(context)
        setNewDataToUpload((mContext), false)
        setLastSync((mContext), 0)
    }
}