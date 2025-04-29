package com.practicum.playlistmaker.media.data.impl

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.data.db.FavoriteDao
import com.practicum.playlistmaker.media.domain.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FavoritesRepositoryImpl(
    private val favoritesDao: FavoriteDao
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {

    }

    override suspend fun removeFromFavorites(track: Track) {

    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return flowOf(emptyList())
    }

    override suspend fun isInFavorites(trackId: Int): Boolean {
        return FavoritesRepository.isInFavorites(trackId)
    }
}