package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response.resultCode == 200) {
            val trackSearchResponse =response as TrackSearchResponse
            trackSearchResponse.results.map { dto ->
                Track(dto.trackId,
                    dto.trackName,
                    dto.artistName,
                    dto.trackTimeMillis,
                    dto.artworkUrl100,
                    dto.collectionName,
                    dto.releaseDate,
                    dto.primaryGenreName,
                    dto.country,
                    dto.previewUrl)  }
        } else {
           emptyList()
        }
    }
}