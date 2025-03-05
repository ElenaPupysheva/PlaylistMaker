package com.practicum.playlistmaker.search.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor

data class SearchUiState(
    val trackList: List<Track> = emptyList(),
    val historyList: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val showHistory: Boolean = false,
    val lastSearchQuery: String = "",
    val stringValue: String = ""
)
class SearchViewModel (
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {private val _uiState = MutableLiveData(SearchUiState())
    val uiState: LiveData<SearchUiState> = _uiState

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable {
        performSearch(_uiState.value?.stringValue.orEmpty())
    }
    private val SEARCH_DEBOUNCE_DELAY = 2000L

    fun onTextChanged(newText: String) {

        updateState { it.copy(stringValue = newText) }
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    fun performSearch(query: String) {
        if (query.isBlank()) {
            return
        }

        updateState {
            it.copy(
                isLoading = true,
                isError = false,
                showHistory = false,
                trackList = emptyList(),
                lastSearchQuery = query
            )
        }


        Thread {
            val result = tracksInteractor.searchTracks(query)
            handler.post {
                if (result.isNotEmpty()) {
                    updateState { current ->
                        current.copy(
                            isLoading = false,
                            trackList = result,
                            isError = false,
                            showHistory = false
                        )
                    }
                } else {
                    updateState { current ->
                        current.copy(
                            isLoading = false,
                            trackList = emptyList(),
                            isError = true,
                            showHistory = false
                        )
                    }
                }
            }
        }.start()
    }

    fun onFocusGained() {
        val currentText = _uiState.value?.stringValue.orEmpty()
        if (currentText.isEmpty()) {
            val history = historyInteractor.getHistory()
            if (history.isNotEmpty()) {
                updateState {
                    it.copy(
                        historyList = history,
                        showHistory = true,
                        isError = false,
                        isLoading = false
                    )
                }
            } else {
                updateState {
                    it.copy(
                        historyList = emptyList(),
                        showHistory = false
                    )
                }
            }
        }
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        updateState {
            it.copy(
                historyList = emptyList(),
                showHistory = false
            )
        }
    }
    fun onTrackClick(track: Track) {
        historyInteractor.addTrack(track)
        val newHistory = historyInteractor.getHistory()
        updateState {
            it.copy(historyList = newHistory)
        }
    }

    private fun updateState(transform: (SearchUiState) -> SearchUiState) {
        val oldState = _uiState.value ?: SearchUiState()
        val newState = transform(oldState)
        _uiState.value = newState
    }
}