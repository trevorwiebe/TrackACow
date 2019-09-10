package com.trevorwiebe.trackacow.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.DeleteAllLocalData;
import com.trevorwiebe.trackacow.dataLoaders.InsertAllLocalChangeToCloud;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.Utility;

public class SettingsFragment extends PreferenceFragmentCompat implements InsertAllLocalChangeToCloud.OnAllLocalDbInsertedToCloud {

    private Context mContext;

    private AlertDialog.Builder mBackingUpData;

    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        Preference versionPref = findPreference("version");
        try {
            if (versionPref != null) {
                versionPref.setSummary(appVersion());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Preference accountNamePref = findPreference("account_name");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String accountName = name + ", " + email;
            if (accountNamePref != null) {
                accountNamePref.setSummary(accountName);
            }
        }

        Preference signOutPref = findPreference("sign_out");
        if (signOutPref != null) {
            signOutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    boolean isThereLocalData = Utility.isThereNewDataToUpload(mContext);
                    if (isThereLocalData) {
                        AlertDialog.Builder thereIsDataDialog = new AlertDialog.Builder(getContext());
                        thereIsDataDialog.setPositiveButton("Backup and sign out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utility.setNewDataToUpload(getContext(), false);
                                mBackingUpData = new AlertDialog.Builder(getContext());
                                View dialogLoadingLayout = LayoutInflater.from(getContext()).inflate(R.layout.dialog_inserting_data_to_cloud, null);
                                mBackingUpData.setView(dialogLoadingLayout);
                                mBackingUpData.show();
                                new InsertAllLocalChangeToCloud(SettingsFragment.this).execute(getContext());
                            }
                        });
                        thereIsDataDialog.setNeutralButton("Sign out anyway", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signOut();
                            }
                        });
                        thereIsDataDialog.setTitle("Information will be lost");
                        thereIsDataDialog.setMessage("There is information that hasn't been saved to the cloud.  Signing out will delete this data.  Backup to cloud to save you data. You must have an internet connection prior to backing up.");
                        thereIsDataDialog.show();
                    } else {
                        signOut();
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        return pInfo.versionName;
    }

    @Override
    public void onAllLocalDbInsertedToCloud(int resultCode) {
        if (resultCode == Constants.SUCCESS) {
            if (mBackingUpData != null) {
                AlertDialog createDialog = mBackingUpData.create();
                createDialog.dismiss();
            }
            signOut();
        } else {
            String message = "";
            switch (resultCode) {
                case Constants.NO_NETWORK_CONNECTION:
                    message = "You do not have a connection, please connect so you can push your data to the cloud.";
                    break;
                case Constants.ERROR_PUSHING_DATA_TO_CLOUD:
                    message = "There was an error pushing your data to the cloud.";
                    break;
                default:
                    message = "An unknown error occurred while pushing your data to the cloud.";
                    break;
            }
            AlertDialog.Builder errorBackingUpDialog = new AlertDialog.Builder(mContext);
            errorBackingUpDialog.setTitle("There was an error backing up your data.");
            errorBackingUpDialog.setMessage(message);
            errorBackingUpDialog.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new InsertAllLocalChangeToCloud(SettingsFragment.this).execute(getContext());
                }
            });
            errorBackingUpDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            errorBackingUpDialog.setNeutralButton("Sign out anyway", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    signOut();
                }
            });
            errorBackingUpDialog.show();
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        if (getActivity() != null) {
            getActivity().finish();
        }
        new DeleteAllLocalData().execute(getContext());
        Utility.setNewDataToUpload(mContext, false);
        Utility.setLastSync(mContext, 0);
    }
}
