package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.domain.models.CLICK_DEBOUNCE_DELAY
import com.practicum.playlistmaker.domain.models.EXTRA_TRACK
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()

    private val trackAdapter = TrackAdapter(mutableListOf())
    private val historyAdapter = TrackAdapter(mutableListOf())
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = trackAdapter

        trackAdapter.setOnTrackClickListener { track -> onTrackClick(track) }
        historyAdapter.setOnTrackClickListener { track -> onTrackClick(track) }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            binding.errorView.visibility = if (state.isError) View.VISIBLE else View.GONE
            binding.errorNet.visibility = View.GONE

            if (state.showHistory) {
                binding.historyView.visibility = View.VISIBLE
                binding.clearSearch.visibility = View.VISIBLE
                binding.recyclerView.adapter = historyAdapter
                historyAdapter.updateTracks(state.historyList)
            } else {
                binding.historyView.visibility = View.GONE
                binding.clearSearch.visibility = View.GONE
                binding.recyclerView.adapter = trackAdapter
                trackAdapter.updateTracks(state.trackList)
            }
        }

        binding.searchEditText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                viewModel.onTextChanged(s.toString())
            }
        })

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
                viewModel.onFocusGained()
            }
        }

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.text.clear()
            binding.clearIcon.visibility = View.GONE
            viewModel.onTextChanged("")
            viewModel.onFocusGained()

            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

        binding.clearSearch.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun onTrackClick(track: Track) {
        viewModel.onTrackClick(track)
        if (clickDebounce()) {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract class SimpleTextWatcher : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: android.text.Editable?) {}
    }
}
