package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.data.dto.PlayerStates
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val _uiState = MutableLiveData<PlayerUiState>(
        PlayerUiState(
            playerState = PlayerStates.DEFAULT,
            currentTime = "0:00"
        )
    )
    val uiState: LiveData<PlayerUiState> = _uiState

    private val updateInterval = 1000L

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(
            url = url,
            onPrepared = {
                _uiState.value = _uiState.value?.copy(
                    playerState = PlayerStates.PREPARED,
                    currentTime = "0:00"
                )
            },
            onCompletion = {
                _uiState.postValue(
                    PlayerUiState(
                        playerState = PlayerStates.PREPARED,
                        currentTime = "0:00"
                    )
                )
            }
        )
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        _uiState.value = _uiState.value?.copy(playerState = PlayerStates.PLAYING)
        startUpdatingProgress()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _uiState.value = _uiState.value?.copy(playerState = PlayerStates.PAUSED)
    }

    fun playbackControl() {
        when (_uiState.value?.playerState) {
            PlayerStates.PLAYING -> pausePlayer()
            PlayerStates.PREPARED, PlayerStates.PAUSED -> startPlayer()
            else -> {}
        }
    }

    private fun startUpdatingProgress() {
        viewModelScope.launch {
            while (_uiState.value?.playerState == PlayerStates.PLAYING) {
                val newTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(playerInteractor.getCurrentPositionMs())

                _uiState.postValue(_uiState.value?.copy(currentTime = newTime))
                delay(updateInterval)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
    }
}