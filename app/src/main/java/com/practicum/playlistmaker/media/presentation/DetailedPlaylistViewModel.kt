package com.practicum.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class DetailedPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<Playlist>()
    val state: LiveData<Playlist> = _state
    private var currentPlaylistId: Long = -1
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun loadPlaylistById(id: Long) {
        currentPlaylistId = id
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(id)
            if (playlist != null) {
                _state.postValue(playlist!!)
                val playlistTracks = playlistInteractor.getTracksForPlaylist(playlist)
                _tracks.postValue(playlistTracks)
            }
        }
    }

    fun removeTrack(track: Track) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(currentPlaylistId)?.let { playlist ->
                playlistInteractor.removeTrackFromPlaylist(playlist, track)
                refreshPlaylistData()
            }
        }
    }

    fun deletePlaylist(playlist: Playlist, onComplete: () -> Unit) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
            onComplete()
        }
    }

    fun refreshPlaylistData() {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(currentPlaylistId)?.let { playlist ->
                _state.postValue(playlist)
                _tracks.postValue(playlistInteractor.getTracksForPlaylist(playlist))
            }
        }
    }

}
