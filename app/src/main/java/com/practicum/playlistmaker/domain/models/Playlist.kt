package com.practicum.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val imagePath: String,
    val trackIds: List<Long> = emptyList(),
    val trackCount: Int = 0
) : Parcelable
