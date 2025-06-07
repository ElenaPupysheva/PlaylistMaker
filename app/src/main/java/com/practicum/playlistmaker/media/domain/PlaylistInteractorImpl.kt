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

    override suspend fun getTracksForPlaylist(playlist: Playlist): List<Track> {
        return repository.getTracksForPlaylist(playlist)
    }

    override suspend fun getTracksByIds(ids: List<Long>): List<Track> {
        return repository.getTracksByIds(ids)
    }

    override suspend fun removeTrackFromPlaylist(playlist: Playlist, track: Track) {
        repository.removeTrackFromPlaylist(playlist, track)
    }

    override suspend fun deleteTrackIfOrphaned(track: Track) {
        repository.deleteTrackIfOrphaned(track)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

}
