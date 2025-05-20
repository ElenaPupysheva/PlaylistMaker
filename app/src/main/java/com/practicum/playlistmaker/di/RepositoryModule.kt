package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.data.converters.TrackConvertor
import com.practicum.playlistmaker.media.data.impl.FavoritesRepositoryImpl
import com.practicum.playlistmaker.media.data.impl.PlaylistRepositoryImpl
import com.practicum.playlistmaker.media.domain.FavoritesRepository
import com.practicum.playlistmaker.media.domain.PlaylistRepository
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.data.impl.HistoryRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(sharedPrefs = get(), gson = get())
    }

    single { TrackConvertor() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get())
    }
}