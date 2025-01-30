package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApiService {
    @GET("search")
    fun searchTracks(
        @Query("term") term: String,
        @Query("results") entity: String = "song"
    ): Call<TrackResponse>
}
