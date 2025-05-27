package com.practicum.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import com.practicum.playlistmaker.media.presentation.PlaylistsState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlistsState = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = playlistsState

    init {
        getAllPlaylists()
    }

    fun getAllPlaylists() {
        playlistsState.postValue(PlaylistsState.Loading)

        viewModelScope.launch {
            playlistInteractor.getPlaylists().collectLatest { playlists ->
                val result = playlists ?: emptyList()
                playlistsState.postValue(
                    if (result.isEmpty()) PlaylistsState.Empty
                    else PlaylistsState.Content(result)
                )
            }
        }
    }
}
