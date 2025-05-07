package com.practicum.playlistmaker.media.data.impl

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.data.converters.TrackConvertor
import com.practicum.playlistmaker.media.data.db.FavoriteDao
import com.practicum.playlistmaker.media.domain.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val favoritesDao: FavoriteDao,
    private val converter: TrackConvertor
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        favoritesDao.insertNewTrack(converter.toEntity(track))
    }

    override suspend fun removeFromFavorites(track: Track) {
        favoritesDao.deleteFavoriteEntity(converter.toEntity(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> =
        favoritesDao.getFavorites()
            .map { list -> list.map(converter::toDomain) }

    override fun observeIsFavorite(trackId: Int): Flow<Boolean> =
        favoritesDao.isInFavorites(trackId)

    override suspend fun isInFavorites(trackId: Int): Boolean =
        favoritesDao.isInFavorites(trackId).first()
}
