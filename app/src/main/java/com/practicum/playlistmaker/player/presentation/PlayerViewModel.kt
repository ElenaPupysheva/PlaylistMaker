package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.data.dto.PlayerStates
import com.practicum.playlistmaker.player.domain.api.AudioPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val playerInteractor: AudioPlayer) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerStates>(PlayerStates.DEFAULT)
    val playerState: LiveData<PlayerStates> get() = _playerState

    private val _currentTime = MutableLiveData<String>("0:00")
    val currentTime: LiveData<String> get() = _currentTime

    private val updateInterval = 1000L

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(
            url = url,
            onPrepared = { _playerState.value = PlayerStates.PREPARED },
            onCompletion = {
                _playerState.value = PlayerStates.PREPARED
                _currentTime.postValue("0:00")
            }
        )
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        _playerState.value = PlayerStates.PLAYING
        startUpdatingProgress()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _playerState.value = PlayerStates.PAUSED
    }

    fun playbackControl() {
        when (_playerState.value) {
            PlayerStates.PLAYING -> pausePlayer()
            PlayerStates.PREPARED, PlayerStates.PAUSED -> startPlayer()
            else -> {}
        }
    }

    private fun startUpdatingProgress() {
        viewModelScope.launch {
            while (_playerState.value == PlayerStates.PLAYING) {
                _currentTime.postValue(
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(playerInteractor.getCurrentPositionMs())
                )
                delay(updateInterval)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
    }
}