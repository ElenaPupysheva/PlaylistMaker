package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun savePlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }

    override suspend fun saveTrackToPlaylistTracks(track: Track) {
        repository.saveTrackToPlaylistTracks(track)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylistsFlow()
    }
}
