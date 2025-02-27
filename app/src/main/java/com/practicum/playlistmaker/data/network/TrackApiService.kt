package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApiService {
    @GET("/search?entity=song")
   fun searchTracks(
        @Query("term") term: String
    ): Call<TrackSearchResponse>
}
