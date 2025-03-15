package com.practicum.playlistmaker.creator

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.player.presentation.AndroidAudioPlayer
import com.practicum.playlistmaker.player.domain.api.AudioPlayer
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.HistoryRepositoryImpl
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl

object Creator {

    private val gson = Gson()
    private lateinit var sharedPrefs: SharedPreferences

    fun init(sharedPreferences: SharedPreferences) {
        sharedPrefs = sharedPreferences
    }



    private val audioPlayer: AudioPlayer by lazy {
        AndroidAudioPlayer()
    }

    val playerInteractor: AudioPlayer by lazy {
        PlayerInteractorImpl(audioPlayer)
    }

    private val historyRepository: HistoryRepository by lazy {
        HistoryRepositoryImpl(sharedPrefs, gson)
    }


}