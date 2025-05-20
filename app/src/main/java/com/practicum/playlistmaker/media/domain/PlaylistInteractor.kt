package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.domain.models.Playlist

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

    suspend fun getPlaylist(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }
}
