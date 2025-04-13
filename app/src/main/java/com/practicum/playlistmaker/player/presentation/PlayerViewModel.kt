package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.data.dto.PlayerState
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val _uiState = MutableLiveData<PlayerUiState>(
        PlayerUiState(
            playerState = PlayerState.Default,
            currentTime = "0:00"
        )
    )
    val uiState: LiveData<PlayerUiState> = _uiState

    private val updateInterval = 300L
    private var updateJob: Job? = null

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(
            url = url,
            onPrepared = {
                _uiState.postValue(
                    PlayerUiState(
                        playerState = PlayerState.Prepared,
                        currentTime = "0:00"
                    )
                )
            },
            onCompletion = {
                stopUpdatingProgress()
                _uiState.postValue(
                    PlayerUiState(
                        playerState = PlayerState.Prepared,
                        currentTime = "0:00"
                    )
                )
            }
        )
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        _uiState.value = _uiState.value?.copy(playerState = PlayerState.Playing)
        startUpdatingProgress()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _uiState.postValue(_uiState.value?.copy(playerState = PlayerState.Paused))
        stopUpdatingProgress()
    }

    fun playbackControl() {
        when (_uiState.value?.playerState) {
            PlayerState.Playing -> pausePlayer()
            PlayerState.Prepared, PlayerState.Paused -> startPlayer()
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
}