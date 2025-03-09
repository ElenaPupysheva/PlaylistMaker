package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track


class TracksInteractorImpl (private val repository: TracksRepository): TracksInteractor {

    override fun searchTracks(expression: String): List<Track> {
            if (expression.isBlank()) return emptyList()
            return repository.searchTracks(expression)
    }
}

