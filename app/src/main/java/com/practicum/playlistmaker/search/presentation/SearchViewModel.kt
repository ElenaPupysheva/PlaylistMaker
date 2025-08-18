package com.practicum.playlistmaker.search.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.ui.SearchError
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class SearchUiState(
    val trackList: List<Track> = emptyList(),
    val historyList: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val error: SearchError? = null,
    val showHistory: Boolean = false,
    val lastSearchQuery: String = "",
    val stringValue: String = ""
)

open class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val _uiState = MutableLiveData(SearchUiState())
    val uiState: LiveData<SearchUiState> = _uiState

    private val handler = Handler(Looper.getMainLooper())
    private val SEARCH_DEBOUNCE_DELAY = 2000L

    private val searchRunnable = Runnable {
        performSearch(_uiState.value?.stringValue.orEmpty())
    }

    fun onTextChanged(newText: String) {
        // сбрасываем прошлую ошибку при наборе текста
        updateState { it.copy(stringValue = newText, isError = false, error = null) }
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun performSearch(query: String) {
        if (query.isBlank()) return

        updateState {
            it.copy(
                isLoading = true,
                isError = false,
                error = null,
                showHistory = false,
                trackList = emptyList(),
                lastSearchQuery = query
            )
        }

        viewModelScope.launch {
            tracksInteractor.searchTracks(query)
                .onStart {
                    updateState { s -> s.copy(isLoading = true, isError = false, error = null) }
                }
                .catch {
                    updateState { s ->
                        s.copy(
                            isLoading = false,
                            trackList = emptyList(),
                            isError = true,
                            error = SearchError.Network,
                            showHistory = false
                        )
                    }
                }
                .collect { tracks ->
                    updateState { s ->
                        if (tracks.isEmpty()) {
                            s.copy(
                                isLoading = false,
                                trackList = emptyList(),
                                isError = true,
                                error = SearchError.NotFound,
                                showHistory = false
                            )
                        } else {
                            s.copy(
                                isLoading = false,
                                trackList = tracks,
                                isError = false,
                                error = null,
                                showHistory = false
                            )
                        }
                    }
                }
        }
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
                        error = null,
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
        updateState { it.copy(historyList = newHistory) }
    }

    private fun updateState(transform: (SearchUiState) -> SearchUiState) {
        val oldState = _uiState.value ?: SearchUiState()
        _uiState.value = transform(oldState)
    }
}