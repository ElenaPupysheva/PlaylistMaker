package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TrackResponse

interface NetworkClient {
    fun doRequest(dto: Any): TrackResponse
}