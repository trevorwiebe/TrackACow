package com.trevorwiebe.trackacow.presentation.fragment_more

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import android.widget.TextView
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.trevorwiebe.trackacow.presentation.manage_pens.ManagePensActivity
import com.trevorwiebe.trackacow.presentation.manage_drugs.ManageDrugsActivity
import com.trevorwiebe.trackacow.presentation.manage_rations.ManageRationsActivity
import com.trevorwiebe.trackacow.presentation.activities.ArchivesActivity
import com.trevorwiebe.trackacow.presentation.activities.SettingsActivity

class MoreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView = inflater.inflate(R.layout.fragment_more, container, false)

        val managePens = rootView.findViewById<TextView>(R.id.manage_pen_tv)
        managePens.setOnClickListener {
            val managePensIntent = Intent(context, ManagePensActivity::class.java)
            startActivity(managePensIntent)
        }

        val manageDrugs = rootView.findViewById<TextView>(R.id.manage_drugs_tv)
        manageDrugs.setOnClickListener {
            val manageDrugsIntent = Intent(context, ManageDrugsActivity::class.java)
            startActivity(manageDrugsIntent)
        }

        val manageRations = rootView.findViewById<TextView>(R.id.manage_rations_tv)
        manageRations.setOnClickListener {
            val manageRationsIntent = Intent(context, ManageRationsActivity::class.java)
            startActivity(manageRationsIntent)
        }

        val archives = rootView.findViewById<TextView>(R.id.archives_tv)
        archives.setOnClickListener {
            val archivesIntent = Intent(context, ArchivesActivity::class.java)
            startActivity(archivesIntent)
        }

        val settings = rootView.findViewById<TextView>(R.id.more_settings_tv)
        settings.setOnClickListener {
            val settingsIntent = Intent(context, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

        return rootView
    }
}