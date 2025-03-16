package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import org.koin.dsl.module
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.models.PRACTICUM_PREFERENCES
import com.practicum.playlistmaker.player.data.impl.AndroidAudioPlayer
import com.practicum.playlistmaker.player.domain.api.AudioRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.TrackApiService
import com.practicum.playlistmaker.search.data.storage.SearchHistoryStorage
import com.practicum.playlistmaker.search.data.storage.SharedPreferencesSearchHistoryStorage
import org.koin.android.ext.koin.androidContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<SharedPreferences> {
        androidContext().getSharedPreferences(PRACTICUM_PREFERENCES, Context.MODE_PRIVATE)

    }

    single<TrackApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApiService::class.java)
    }

    factory { Gson() }

    single { MediaPlayer() }

    single<AudioRepository> {
        AndroidAudioPlayer(get())
    }

    single { PlayerInteractorImpl(get()) }

    single<SearchHistoryStorage> {
        SharedPreferencesSearchHistoryStorage(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

}