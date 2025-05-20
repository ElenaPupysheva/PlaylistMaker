package com.practicum.playlistmaker.media.data.impl

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.media.data.converters.PlaylistConvertor
import com.practicum.playlistmaker.media.data.db.PlaylistDao
import com.practicum.playlistmaker.media.data.db.PlaylistEntity
import com.practicum.playlistmaker.media.domain.PlaylistRepository


class PlaylistRepositoryImpl(
    private val dao: PlaylistDao,

    ) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        dao.insertPlaylist(playlist.toEntity())
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        dao.updatePlaylist(playlist.toEntity())
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return dao.getAllPlaylists().map { it.toDomain() }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return dao.getPlaylistById(id)?.toDomain()
    }

    private fun Playlist.toEntity(): PlaylistEntity {
        return PlaylistEntity(
            id = id,
            name = name,
            description = description,
            imagePath = imagePath,
            trackIdsJson = PlaylistConvertor().fromList(trackIds),
            trackCount = trackCount
        )
    }

    private fun PlaylistEntity.toDomain(): Playlist {
        return Playlist(
            id = id,
            name = name,
            description = description,
            imagePath = imagePath,
            trackIds = PlaylistConvertor().toList(trackIdsJson),
            trackCount = trackCount
        )
    }
}
