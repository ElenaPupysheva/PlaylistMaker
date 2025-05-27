package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.domain.FavoritesInteractor
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import com.practicum.playlistmaker.player.data.dto.PlayerState
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val playerInteractor: PlayerInteractor,
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

    private val updateInterval = 300L
    private var updateJob: Job? = null
    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(
            url = url,
            onPrepared = {
                _uiState.postValue(
                    PlayerUiState(
                        playerState = PlayerState.Prepared(),
                        currentTime = "0:00"
                    )
                )
            },
            onCompletion = {
                stopUpdatingProgress()
                _uiState.postValue(
                    PlayerUiState(
                        playerState = PlayerState.Prepared(),
                        currentTime = "0:00"
                    )
                )
            }
        )
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        _uiState.value = _uiState.value?.copy(playerState = PlayerState.Playing())
        startUpdatingProgress()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _uiState.postValue(_uiState.value?.copy(playerState = PlayerState.Paused()))
        stopUpdatingProgress()
    }

    fun playbackControl() {
        when (_uiState.value?.playerState) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared,
            is PlayerState.Paused -> startPlayer()
            else -> {}
        }
    }

    private fun stopUpdatingProgress() {
        updateJob?.cancel()
        updateJob = null
    }

    private fun startUpdatingProgress() {
        stopUpdatingProgress()

        updateJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                val newTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(playerInteractor.getCurrentPositionMs())

                _uiState.postValue(_uiState.value?.copy(currentTime = newTime))
                delay(updateInterval)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdatingProgress()
        playerInteractor.releasePlayer()
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

    val playlists: StateFlow<List<Playlist>> = playlistInteractor.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


}