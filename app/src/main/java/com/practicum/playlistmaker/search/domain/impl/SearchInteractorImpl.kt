package com.practicum.playlistmaker.search.domain.impl
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchInteractorImpl(
    private val repository: TracksRepository
    ): SearchInteractor {
        override fun searchTracks(expression: String): List<Track>{
            if (expression.isBlank()) return emptyList()

            return repository.searchTracks(expression)
        }
    }
