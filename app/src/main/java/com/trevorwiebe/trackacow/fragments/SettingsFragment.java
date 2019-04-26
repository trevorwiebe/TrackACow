package com.trevorwiebe.trackacow.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.DeleteAllLocalData;
import com.trevorwiebe.trackacow.dataLoaders.InsertAllLocalChangeToCloud;
import com.trevorwiebe.trackacow.utils.Utility;

public class SettingsFragment extends PreferenceFragmentCompat implements InsertAllLocalChangeToCloud.OnAllLocalDbInsertedToCloud {

    private Context mContext;

    private AlertDialog.Builder mBackingUpData;


    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        Preference versionPref = findPreference("version");
        try {
            versionPref.setSummary(appVersion());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Preference accountNamePref = findPreference("account_name");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String accountName = name + ", " + email;
            accountNamePref.setSummary(accountName);
        }

        Preference subscriptionPref = findPreference("subscription");
        subscriptionPref.setVisible(false);

        Preference signOutPref = findPreference("sign_out");
        signOutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean isThereLocalData = Utility.isThereNewDataToUpload(getContext());
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
                            Utility.setNewDataToUpload(getContext(), false);
                            new DeleteAllLocalData().execute(getContext());
                            FirebaseAuth.getInstance().signOut();
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        }
                    });
                    thereIsDataDialog.setTitle("Information will be lost");
                    thereIsDataDialog.setMessage("There is information that hasn't been saved to the cloud.  Signing out will delete this data.  Backup to cloud to save you data. You must have an internet connection prior to backing up.");
                    thereIsDataDialog.show();
                } else {
                    Utility.setNewDataToUpload(getContext(), false);
                    new DeleteAllLocalData().execute(getContext());
                    FirebaseAuth.getInstance().signOut();
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        String version = pInfo.versionName;
        return version;

    }

    @Override
    public void onAllLocalDbInsertedToCloud(int resultCode) {
        if (mBackingUpData != null) {
            AlertDialog createDialog = mBackingUpData.create();
            createDialog.dismiss();
        }
        FirebaseAuth.getInstance().signOut();
        if (getActivity() != null) {
            getActivity().finish();
        }
        Utility.setNewDataToUpload(getContext(), false);
    }
}
