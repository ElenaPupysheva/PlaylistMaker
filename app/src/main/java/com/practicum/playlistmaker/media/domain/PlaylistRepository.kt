package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.domain.models.Playlist

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylists(): List<Playlist>
    suspend fun getPlaylistById(id: Long): Playlist?
}
