package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun addToFavorites(track: Track) {
        repository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: Track) {
        repository.removeFromFavorites(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override suspend fun isInFavorites(trackId: Int): Boolean {
        return repository.isInFavorites(trackId)
    }

    override fun observeIsFavorite(trackId: Int) = repository.observeIsFavorite(trackId)

    override suspend fun toggle(track: Track) {
        if (repository.isInFavorites(track.trackId)) repository.removeFromFavorites(track)
        else repository.addToFavorites(track)
    }

}