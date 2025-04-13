package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class TracksInteractorImpl (private val repository: TracksRepository): TracksInteractor {

    override fun searchTracks(expression: String): Flow<List<Track>> =
        (if (expression.isBlank()) flow { emit(emptyList<Track>()) }
        else repository.searchTracks(expression)) as Flow<List<Track>>
}

