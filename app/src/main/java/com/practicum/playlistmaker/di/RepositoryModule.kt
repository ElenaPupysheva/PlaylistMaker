package com.practicum.playlistmaker.di

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
}