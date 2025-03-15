package com.practicum.playlistmaker.search.data.network

import android.content.Context
import com.practicum.playlistmaker.search.data.dto.TrackResponse
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(
    private val iTunesService: TrackApiService
) : NetworkClient {
    override fun doRequest(dto: Any): TrackResponse {
        if (dto is TracksSearchRequest) {
            val resp = iTunesService.searchTracks(dto.expression).execute()

            val body = resp.body() ?: TrackSearchResponse(
                resultCount = 0,
                results = emptyList()
            )

            return body.apply { resultCode = resp.code() }

        } else {
            return TrackResponse().apply { resultCode = 400 }
        }
    }
}