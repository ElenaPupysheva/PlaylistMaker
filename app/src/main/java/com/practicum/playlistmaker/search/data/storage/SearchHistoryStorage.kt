package com.practicum.playlistmaker.search.data.storage

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryStorage {
    fun getHistory(): List<Track>
    fun saveHistory(history: List<Track>)
    fun clearHistory()
}
