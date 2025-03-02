package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class HistoryInteractorImpl(
    private val historyRepository :HistoryRepository): HistoryInteractor {
    override fun getHistory(): List<Track> {
        return historyRepository.getHistory()
    }

    override fun addTrack(track: Track) {
        historyRepository.addTrack(track)
    }
    override fun clearHistory() {
        historyRepository.clearHistory()
    }
}