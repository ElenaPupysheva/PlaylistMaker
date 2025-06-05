package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun savePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getAllPlaylists(): List<Playlist>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun saveTrackToPlaylistTracks(track: Track)
    fun getPlaylists(): Flow<List<Playlist>>
}
