package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<List<Track>>


}