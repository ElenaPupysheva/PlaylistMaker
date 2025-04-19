package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.TrackResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): TrackResponse
}