package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylists(): List<Playlist>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun saveTrackToPlaylistTracks(track: Track)
    fun getPlaylistsFlow(): Flow<List<Playlist>>
    suspend fun getTracksByIds(trackIds: List<Long>): List<Track>
    suspend fun getTracksForPlaylist(playlist: Playlist): List<Track>
    suspend fun removeTrackFromPlaylist(playlist: Playlist, track: Track)
    suspend fun deleteTrackIfOrphaned(track: Track)
    suspend fun deletePlaylist(playlist: Playlist)
}
