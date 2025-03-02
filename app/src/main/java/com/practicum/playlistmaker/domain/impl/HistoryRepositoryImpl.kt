package com.practicum.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class HistoryRepositoryImpl (
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) : HistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    override fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(HISTORY_KEY, null) ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    override fun setHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeLast()
        }
        setHistory(history)
    }

    override fun clearHistory() {
        sharedPrefs.edit().remove(HISTORY_KEY).apply()
    }


}