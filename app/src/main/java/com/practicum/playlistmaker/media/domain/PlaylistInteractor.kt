package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractor(private val repository: PlaylistRepository) {

    suspend fun savePlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    suspend fun getAllPlaylists(): List<Playlist> {
        return repository.getPlaylists()
    }


    suspend fun saveTrackToPlaylistTracks(track: Track) {
        repository.saveTrackToPlaylistTracks(track)
    }

    fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylistsFlow()
    }
}

