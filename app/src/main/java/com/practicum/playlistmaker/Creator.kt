package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.data.player.AndroidAudioPlayer
import com.practicum.playlistmaker.domain.api.AudioPlayer
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.HistoryRepositoryImpl
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private val gson = Gson()
    private lateinit var sharedPrefs: SharedPreferences

    fun init(sharedPreferences: SharedPreferences) {
        this.sharedPrefs = sharedPreferences
    }

    private val networkClient: NetworkClient by lazy {
        RetrofitNetworkClient()
    }

    private val tracksRepository: TracksRepository by lazy {
        TracksRepositoryImpl(networkClient)
    }

    val tracksInteractor: TracksInteractor by lazy {
        TracksInteractorImpl(tracksRepository)
    }


    private val audioPlayer: AudioPlayer by lazy {
        AndroidAudioPlayer()
    }

    val playerInteractor: PlayerInteractor by lazy {
        PlayerInteractorImpl(audioPlayer)
    }

    private val historyRepository: HistoryRepository by lazy {
        HistoryRepositoryImpl(sharedPrefs, gson)
    }

    val historyInteractor: HistoryInteractor by lazy {
        HistoryInteractorImpl(historyRepository)
    }
}