package com.practicum.playlistmaker.media.data.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.data.db.PlaylistTrackEntity

class PlaylistConvertor {
    private val gson = Gson()

    fun fromList(list: List<Long>): String {
        return gson.toJson(list)
    }

    fun toList(json: String): List<Long> {
        val type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(json, type)
    }
    fun mapToTrack(entity: PlaylistTrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }


}