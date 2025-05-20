package com.practicum.playlistmaker.media.data.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistConvertor {
    private val gson = Gson()

    fun fromList(list: List<Long>): String {
        return gson.toJson(list)
    }

    fun toList(json: String): List<Long> {
        val type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(json, type)
    }
}