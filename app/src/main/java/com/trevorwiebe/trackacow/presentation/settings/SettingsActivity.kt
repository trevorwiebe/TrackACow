package com.trevorwiebe.trackacow.presentation.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}