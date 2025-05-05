package com.practicum.playlistmaker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
import org.koin.dsl.module
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.models.PRACTICUM_PREFERENCES
import com.practicum.playlistmaker.media.data.converters.TrackConvertor
import com.practicum.playlistmaker.media.data.db.AppDatabase
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
import com.practicum.playlistmaker.media.data.db.FavoriteDao
import com.practicum.playlistmaker.media.data.impl.FavoritesRepositoryImpl
import com.practicum.playlistmaker.media.domain.FavoritesRepository

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

    single {
        Room.databaseBuilder(
            get<Application>(),
            AppDatabase::class.java,
            "favorite_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single<FavoriteDao> {
        get<AppDatabase>().favoriteDao()
    }
    single { TrackConvertor() }

    single<FavoritesRepository> { FavoritesRepositoryImpl(get(), get()) }


}