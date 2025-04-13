package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.search.data.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Flow<List<Track>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response is TrackSearchResponse && response.resultCode == 200) {
            val tracks = response.results.map { dto ->
                Track(dto.trackId,
                    dto.trackName,
                    dto.artistName,
                    dto.trackTimeMillis,
                    dto.artworkUrl100,
                    dto.collectionName,
                    dto.releaseDate,
                    dto.primaryGenreName,
                    dto.country,
                    dto.previewUrl)  }
            emit(tracks)
        } else {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}

