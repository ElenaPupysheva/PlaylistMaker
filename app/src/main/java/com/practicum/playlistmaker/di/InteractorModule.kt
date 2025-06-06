package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.data.impl.PlaylistRepositoryImpl
import com.practicum.playlistmaker.media.domain.FavoritesInteractor
import com.practicum.playlistmaker.media.domain.FavoritesInteractorImpl
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.PlaylistInteractorImpl
import com.practicum.playlistmaker.media.domain.PlaylistRepository
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single<PlaylistInteractor> { PlaylistInteractorImpl(get()) }
}
