package com.trevorwiebe.trackacow.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        LinearLayout archives = rootView.findViewById(R.id.archive_layout);
        archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent archivesIntent = new Intent(getContext(), ArchivesActivity.class);
                startActivity(archivesIntent);
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
