package com.trevorwiebe.trackacow.fragments;

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
import com.trevorwiebe.trackacow.activities.ManageDrugsActivity;
import com.trevorwiebe.trackacow.activities.ManagePensActivity;
import com.trevorwiebe.trackacow.activities.SettingsActivity;

public class MoreFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.more_fragment, container, false);

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

        LinearLayout signOut = rootView.findViewById(R.id.signout_layout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        return rootView;
    }


}
