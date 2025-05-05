package com.practicum.playlistmaker.media.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.domain.FavoritesInteractor
import com.practicum.playlistmaker.utils.SingleEvent
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.launch


class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private var isClickAllowed = true

    private val _state = MutableLiveData<FavoriteState>()
    val state: LiveData<FavoriteState> get() = _state

    private val _onTrackClickTrigger = SingleEvent<Track>()
    val onTrackClickTrigger: LiveData<Track> get() = _onTrackClickTrigger

    fun loadFavorites() {
        renderState(FavoriteState.Loading)
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks()
                .collect { processResult(it) }
        }
    }

    private fun processResult(tracks: List<Track>) {
        _state.postValue(
            if (tracks.isEmpty()) FavoriteState.Empty else FavoriteState.Content(tracks)
        )
    }

    fun clickDebounce(track: Track) {
        if (isClickAllowed) {
            isClickAllowed = false
            _onTrackClickTrigger.value = track
            debounceClick(Unit)
        }
    }

    private val debounceClick = debounce<Unit>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) {
        isClickAllowed = true
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private fun renderState(state: FavoriteState) {
        _state.postValue(state)
    }
}
