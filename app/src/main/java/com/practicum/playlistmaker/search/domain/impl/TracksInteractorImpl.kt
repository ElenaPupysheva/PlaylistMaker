package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.data.network.TrackApiService
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class TracksInteractorImpl (private val repository: TracksRepository): TracksInteractor {

    private val urlMusic: String = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(urlMusic)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackApiService = retrofit.create(TrackApiService::class.java)

    override fun searchTracks(expression: String): List<Track> {
            if (expression.isBlank()) return emptyList()

            return repository.searchTracks(expression)
    }
}

