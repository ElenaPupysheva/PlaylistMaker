package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.domain.FavoritesInteractor
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import com.practicum.playlistmaker.player.data.dto.PlayerState
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
private val playerInteractor: PlayerInteractor,
private val favoritesInteractor: FavoritesInteractor,
private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private var wasPrepared = false
    private val _uiState = MutableStateFlow(
        PlayerUiState(
            playerState = PlayerState.Default(),
            currentTime = "00:00"
        )
    )
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    val playlists = playlistInteractor.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val updateInterval = 300L
    private var updateJob: Job? = null

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        playerInteractor.playerStateFlow
            .onEach { state ->
                _uiState.value = _uiState.value.copy(playerState = state)

                when (state) {
                    is PlayerState.Playing -> startUpdatingProgress()
                    is PlayerState.Paused,
                    is PlayerState.Prepared,
                    is PlayerState.Complete,
                    is PlayerState.Default -> {
                        if (state is PlayerState.Complete || state is PlayerState.Prepared) {
                            _uiState.value = _uiState.value.copy(currentTime = "00:00")
                        }
                        stopUpdatingProgress()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun preparePlayer(url: String) {
        if (wasPrepared) {
            return
        }
        playerInteractor.preparePlayer(url)
        wasPrepared = true
    }

    fun playbackControl(url: String) {
        when (uiState.value.playerState) {
            is PlayerState.Prepared -> playerInteractor.startPlayer()
            is PlayerState.Playing -> playerInteractor.pausePlayer()
            is PlayerState.Paused -> playerInteractor.startPlayer()
            is PlayerState.Complete -> {
                wasPrepared = false
                preparePlayer(url)
            }

            else -> {}
        }
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
    }

    private fun startUpdatingProgress() {
        if (updateJob != null) return
        updateJob = viewModelScope.launch {
            while (isActive) {
                val pos = playerInteractor.getCurrentPositionMs()
                val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(pos)
                _uiState.value = _uiState.value.copy(currentTime = time)
                delay(updateInterval)
            }
        }
    }

    private fun stopUpdatingProgress() {
        updateJob?.cancel()
        updateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdatingProgress()
    }

    fun observeFavorite(trackId: Int) {
        viewModelScope.launch {
            favoritesInteractor.observeIsFavorite(trackId)
                .collectLatest { fav -> _isFavorite.value = fav }
        }
    }

    fun onLikeClicked(track: Track) = viewModelScope.launch {
        favoritesInteractor.toggle(track)
    }
}