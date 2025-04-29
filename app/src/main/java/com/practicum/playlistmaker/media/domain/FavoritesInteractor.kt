package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun isInFavorites(trackId: Int): Boolean
}