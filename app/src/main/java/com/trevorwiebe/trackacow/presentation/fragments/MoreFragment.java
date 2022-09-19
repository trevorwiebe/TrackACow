package com.trevorwiebe.trackacow.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.presentation.activities.ArchivesActivity;
import com.trevorwiebe.trackacow.presentation.activities.ManageDrugsActivity;
import com.trevorwiebe.trackacow.presentation.activities.ManagePensActivity;
import com.trevorwiebe.trackacow.presentation.activities.ManageRationsActivity;
import com.trevorwiebe.trackacow.presentation.activities.SettingsActivity;

public class MoreFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        TextView managePens = rootView.findViewById(R.id.manage_pen_tv);
        managePens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent managePensIntent = new Intent(getContext(), ManagePensActivity.class);
                startActivity(managePensIntent);
            }
        });

        TextView manageDrugs = rootView.findViewById(R.id.manage_drugs_tv);
        manageDrugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manageDrugsIntent = new Intent(getContext(), ManageDrugsActivity.class);
                startActivity(manageDrugsIntent);
            }
        });

        TextView manageRations = rootView.findViewById(R.id.manage_rations_tv);
        manageRations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent manageRations = new Intent(getContext(), ManageRationsActivity.class);
                startActivity(manageRations);
            }
        });

        TextView archives = rootView.findViewById(R.id.archives_tv);
        archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent archivesIntent = new Intent(getContext(), ArchivesActivity.class);
                startActivity(archivesIntent);
            }
        });

        TextView settings = rootView.findViewById(R.id.more_settings_tv);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        return rootView;
    }
}
