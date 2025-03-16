package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.models.CLICK_DEBOUNCE_DELAY
import com.practicum.playlistmaker.domain.models.EXTRA_TRACK
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity(), TrackAdapter.OnTrackClickListener {
    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private val trackAdapter = TrackAdapter(mutableListOf())
    private val historyAdapter = TrackAdapter(mutableListOf())
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = trackAdapter

        trackAdapter.setOnTrackClickListener(this)
        historyAdapter.setOnTrackClickListener(this)

        viewModel.uiState.observe(this) { state ->
            binding.progressBar.visibility = if (state.isLoading) android.view.View.VISIBLE else android.view.View.GONE
            if (state.isError) {
                binding.errorView.visibility = android.view.View.VISIBLE
                binding.errorNet.visibility = android.view.View.GONE
            } else {
                binding.errorView.visibility = android.view.View.GONE
                binding.errorNet.visibility = android.view.View.GONE
            }
            if (state.showHistory) {
                binding.historyView.visibility = android.view.View.VISIBLE
                binding.clearSearch.visibility = android.view.View.VISIBLE
                binding.recyclerView.adapter = historyAdapter
                historyAdapter.updateTracks(state.historyList)
            } else {
                binding.historyView.visibility = android.view.View.GONE
                binding.clearSearch.visibility = android.view.View.GONE
                binding.recyclerView.adapter = trackAdapter
                trackAdapter.updateTracks(state.trackList)
            }
        }
        binding.searchEditText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) android.view.View.GONE else android.view.View.VISIBLE
                viewModel.onTextChanged(s.toString())
            }
        })
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
                viewModel.onFocusGained()
            }
        }
        binding.clearIcon.setOnClickListener {
            binding.searchEditText.text.clear()
            binding.clearIcon.visibility = android.view.View.GONE
            viewModel.onTextChanged("")
            viewModel.onFocusGained()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.performSearch(binding.searchEditText.text.toString())
                true
            } else false
        }
        binding.refreshButton.setOnClickListener {
            val last = viewModel.uiState.value?.lastSearchQuery.orEmpty()
            binding.searchEditText.setText(last)
            viewModel.performSearch(last)
        }
        binding.clearSearch.setOnClickListener { viewModel.clearHistory() }
    }

    override fun onTrackClick(track: Track) {
        viewModel.onTrackClick(track)
        if (clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(EXTRA_TRACK, Gson().toJson(track))
            startActivity(intent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            binding.root.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
            isClickAllowed = false
        }
        return current
    }
}

abstract class SimpleTextWatcher : android.text.TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: android.text.Editable?) {}
}
