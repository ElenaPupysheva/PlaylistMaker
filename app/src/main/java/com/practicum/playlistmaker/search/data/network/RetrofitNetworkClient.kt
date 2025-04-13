package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.TrackResponse
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest

class RetrofitNetworkClient(
    private val iTunesService: TrackApiService
) : NetworkClient {
    override suspend fun doRequest(dto: Any): TrackResponse {
        return if (dto is TracksSearchRequest) {
            val response = iTunesService.searchTracks(dto.expression)
            response.apply { resultCode = 200 }
        } else {
            TrackResponse().apply { resultCode = 400 }
        }
    }
}