package com.practicum.playlistmaker.main.ui
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.main.presentation.MainViewModel
import com.practicum.playlistmaker.main.presentation.NavigationEvent
import com.practicum.playlistmaker.media.ui.MediaActivity
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButtons()
        observeViewModel()

    }

    private fun setupButtons() {
        binding.searchButton.setOnClickListener {
            viewModel.onSearchClicked()
        }
        binding.mediaButton.setOnClickListener {
            viewModel.onMediaClicked()
        }
        binding.settingButton.setOnClickListener {
            viewModel.onSettingsClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                NavigationEvent.OpenSearch -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                }

                NavigationEvent.OpenMedia -> {
                    startActivity(Intent(this, MediaActivity::class.java))
                }

                NavigationEvent.OpenSettings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            }
        }
    }
}
