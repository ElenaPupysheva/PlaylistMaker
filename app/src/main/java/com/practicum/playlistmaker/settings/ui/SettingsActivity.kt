package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)



        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.darkThemeEnabled.observe(this) { isEnabled ->
            val app = applicationContext as App
            if (app.darkTheme != isEnabled) {
                app.switchTheme(isEnabled)
            }
            binding.themeSwitcher.isChecked = isEnabled

        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeToggled(isChecked)
        }


        binding.appShare.setOnClickListener {
            val shareMessage = getString(R.string.link_course)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                type = "text/plain"
            }
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_text))
            startActivity(chooser)
        }

        binding.mailSupport.setOnClickListener {
            val message = getString(R.string.message_text)
            val theme = getString(R.string.theme_text)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.link_mail)))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, theme)
            startActivity(shareIntent)
        }

        binding.userAgreement.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_agreement)))
            startActivity(browserIntent)
        }
    }
}
