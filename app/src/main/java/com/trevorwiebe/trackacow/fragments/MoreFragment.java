package com.trevorwiebe.trackacow.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.activities.ArchivesActivity;
import com.trevorwiebe.trackacow.activities.ManageDrugsActivity;
import com.trevorwiebe.trackacow.activities.ManagePensActivity;
import com.trevorwiebe.trackacow.activities.SettingsActivity;
import com.trevorwiebe.trackacow.dataLoaders.DeleteAllLocalData;
import com.trevorwiebe.trackacow.dataLoaders.InsertAllLocalChangeToCloud;
import com.trevorwiebe.trackacow.utils.Utility;

public class MoreFragment extends Fragment implements InsertAllLocalChangeToCloud.OnAllLocalDbInsertedToCloud {

    private static final String TAG = "MoreFragment";

    private AlertDialog.Builder mBackingUpData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        LinearLayout manageDrugs = rootView.findViewById(R.id.manage_drugs_layout);
        manageDrugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manageDrugsIntent = new Intent(getContext(), ManageDrugsActivity.class);
                startActivity(manageDrugsIntent);
            }
        });

        LinearLayout managePens = rootView.findViewById(R.id.manage_pens_layout);
        managePens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent managePensIntent = new Intent(getContext(), ManagePensActivity.class);
                startActivity(managePensIntent);
            }
        });

        LinearLayout settings = rootView.findViewById(R.id.settings_layout);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        LinearLayout archives = rootView.findViewById(R.id.archive_layout);
        archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent archivesIntent = new Intent(getContext(), ArchivesActivity.class);
                startActivity(archivesIntent);
            }
        });

        LinearLayout signOut = rootView.findViewById(R.id.signout_layout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            new InsertAllLocalChangeToCloud(MoreFragment.this).execute(getContext());
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
            }
        });

        return rootView;
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
