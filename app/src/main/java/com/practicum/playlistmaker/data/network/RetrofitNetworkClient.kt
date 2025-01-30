package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TrackResponse
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val ITunesURL: String = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITunesURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val imdbService = retrofit.create(ITunesAPI::class.java)

    override fun doRequest(dto: Any): TrackResponse {
        if (dto is TracksSearchRequest) {
            val resp = imdbService.searchMovies(dto.expression).execute()

            val body = resp.body() ?: TrackResponse()

            return body.apply { resultCode = resp.code() }
        } else {
            return TrackResponse().apply { resultCode = 400 }
        }
    }
}