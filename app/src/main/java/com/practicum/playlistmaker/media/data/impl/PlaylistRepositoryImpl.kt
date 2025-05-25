package com.practicum.playlistmaker.media.data.impl

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.data.converters.PlaylistConvertor
import com.practicum.playlistmaker.media.data.converters.toPlaylistTrackEntity
import com.practicum.playlistmaker.media.data.db.PlaylistDao
import com.practicum.playlistmaker.media.data.db.PlaylistEntity
import com.practicum.playlistmaker.media.data.db.PlaylistTrackDao
import com.practicum.playlistmaker.media.domain.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao
) : PlaylistRepository {

    private val playlistConvertor = PlaylistConvertor()

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlist.toEntity())
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist.toEntity())
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return playlistDao.getAllPlaylists().map { it.toDomain() }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistDao.getPlaylistById(id)?.toDomain()
    }

    override suspend fun saveTrackToPlaylistTracks(track: Track) {
        playlistTrackDao.insertTrack(track.toPlaylistTrackEntity())
    }

    override fun getPlaylistsFlow(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylistsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }


    private fun Playlist.toEntity(): PlaylistEntity {
        return PlaylistEntity(
            id = id,
            name = name,
            description = description,
            imagePath = imagePath,
            trackIdsJson = playlistConvertor.fromList(trackIds),
            trackCount = trackCount
        )
    }

    private fun PlaylistEntity.toDomain(): Playlist {
        return Playlist(
            id = id,
            name = name,
            description = description,
            imagePath = imagePath,
            trackIds = playlistConvertor.toList(trackIdsJson),
            trackCount = trackCount
        )
    }
}
