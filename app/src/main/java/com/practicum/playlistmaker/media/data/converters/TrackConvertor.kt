package com.practicum.playlistmaker.media.data.converters

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.data.db.FavoriteEntity
import com.practicum.playlistmaker.media.data.db.PlaylistTrackEntity

class TrackConvertor {

    fun toEntity(track: Track): FavoriteEntity = FavoriteEntity(
        track.trackId,
        track.trackName,
        track.artistName,
        track.trackTimeMillis,
        track.artworkUrl100,
        track.collectionName,
        track.releaseDate,
        track.primaryGenreName,
        track.country,
        track.previewUrl,
        System.currentTimeMillis()
    )

    fun toDomain(entity: FavoriteEntity): Track = Track(
        entity.trackId,
        entity.trackName,
        entity.artistName,
        entity.trackTimeMillis,
        entity.artworkUrl100,
        entity.collectionName,
        entity.releaseDate,
        entity.primaryGenreName,
        entity.country,
        entity.previewUrl
    )

}