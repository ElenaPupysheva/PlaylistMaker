package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.domain.FavoritesInteractor
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import com.practicum.playlistmaker.player.data.dto.PlayerState
import com.practicum.playlistmaker.player.data.service.MusicService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
                      private val favoritesInteractor: FavoritesInteractor,
                      private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _uiState = MutableLiveData<PlayerUiState>(
        PlayerUiState(
            playerState = PlayerState.Default(),
            currentTime = "0:00"
        )
    )
    val uiState: LiveData<PlayerUiState> = _uiState
    private fun formatTime(ms: Int): String {
        val minutes = (ms / 1000) / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    val playlists = playlistInteractor.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    private val updateInterval = 300L
    private var updateJob: Job? = null
    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite
    private var musicService: MusicService? = null

    internal fun attachService(service: MusicService) {
        musicService = service
    }
    fun preparePlayer(url: String) {
        musicService?.preparePlayer(url)
    }

    fun updatePlayerStateFromService(state: PlayerState) {
        _uiState.postValue(_uiState.value?.copy(playerState = state))

        when (state) {
            is PlayerState.Playing -> startUpdatingProgress()
            is PlayerState.Paused, is PlayerState.Prepared, is PlayerState.Default -> stopUpdatingProgress()
        }
    }

    fun playbackControl() {
        when (_uiState.value?.playerState) {
            is PlayerState.Playing -> musicService?.pausePlayer()
            is PlayerState.Paused, is PlayerState.Prepared -> musicService?.startPlayer()
            else -> {}
        }
    }

    private fun stopUpdatingProgress() {
        updateJob?.cancel()
        updateJob = null
    }

    private fun startUpdatingProgress() {
        if (updateJob != null) return
        stopUpdatingProgress()
        updateJob = viewModelScope.launch {
            while (true) {
                val pos = musicService?.getCurrentPlayerPosition() ?: 0
                val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(pos)
                _uiState.postValue(_uiState.value?.copy(currentTime = time))
                delay(updateInterval)
            }
        }
    }

    fun pausePlayer() {
        musicService?.pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdatingProgress()
    }

    fun observeFavorite(trackId: Int) {
        viewModelScope.launch {
            favoritesInteractor.observeIsFavorite(trackId)
                .collectLatest { fav -> _isFavorite.postValue(fav) }
        }
    }

    fun onLikeClicked(track: Track) = viewModelScope.launch {
        favoritesInteractor.toggle(track)
    }
}