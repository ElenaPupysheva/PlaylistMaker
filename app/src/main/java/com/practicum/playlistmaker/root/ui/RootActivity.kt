package com.practicum.playlistmaker.root.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.SettingsFragment

class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)


        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                this.add(R.id.rootFragmentSettingsView, SettingsFragment())
            }
        }
    }

}