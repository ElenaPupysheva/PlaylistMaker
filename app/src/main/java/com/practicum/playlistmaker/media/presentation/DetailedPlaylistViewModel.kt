package com.practicum.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class DetailedPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<Playlist>()
    val state: LiveData<Playlist> = _state

    fun loadPlaylistById(id: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(id)
            _state.postValue(playlist)
        }
    }
}