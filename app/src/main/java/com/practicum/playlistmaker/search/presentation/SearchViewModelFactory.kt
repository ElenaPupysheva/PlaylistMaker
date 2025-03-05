package com.practicum.playlistmaker.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor

class SearchViewModelFactory(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(tracksInteractor, historyInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}