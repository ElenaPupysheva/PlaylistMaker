package com.practicum.playlistmaker.media.data.converters

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.data.db.PlaylistTrackEntity

fun Track.toPlaylistTrackEntity() = PlaylistTrackEntity(
    trackId,
    trackName,
    artistName,
    trackTimeMillis,
    artworkUrl100,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    previewUrl
)
