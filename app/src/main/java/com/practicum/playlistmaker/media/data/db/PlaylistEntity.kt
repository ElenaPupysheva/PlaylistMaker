package com.practicum.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val imagePath: String,
    val trackIdsJson: String,
    val trackCount: Int,
)