package com.practicum.playlistmaker

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApiService {
    @GET("search")
    fun searchTracks(
        @Query("term") term: String,
        @Query("results") entity: String = "song"
    ): Call<TrackResponse>
}
