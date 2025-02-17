package com.practicum.playlistmaker.ui.tracks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.PRACTICUM_PREFERENCES
import com.practicum.playlistmaker.domain.models.SWITCH_KEY


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val sharedPrefs = getSharedPreferences(PRACTICUM_PREFERENCES, MODE_PRIVATE)
        val isDarkTheme = sharedPrefs.getBoolean(SWITCH_KEY, false)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.isChecked = isDarkTheme

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            sharedPrefs.edit().putBoolean(SWITCH_KEY, isChecked).apply()
        }

        // Enable the navigation icon (back button)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val appSharing = findViewById<TextView>(R.id.appShare)
        appSharing.setOnClickListener {
            val shareMessage = getString(R.string.link_course)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                type = "text/plain"
            }
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_text))
            startActivity(chooser)
        }

        val mailSend = findViewById<TextView>(R.id.mailSupport)
        mailSend.setOnClickListener {
            val message = getString(R.string.message_text)
            val theme = getString(R.string.theme_text)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.link_mail)))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, theme)
            startActivity(shareIntent)
        }

        val agreementOpen = findViewById<TextView>(R.id.userAgreement)
        agreementOpen.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_agreement)))
            startActivity(browserIntent)
        }
    }
}
