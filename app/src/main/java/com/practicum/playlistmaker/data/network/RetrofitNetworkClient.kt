package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TrackResponse
import com.practicum.playlistmaker.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val urlMusic: String = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(urlMusic)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(TrackApiService::class.java)

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