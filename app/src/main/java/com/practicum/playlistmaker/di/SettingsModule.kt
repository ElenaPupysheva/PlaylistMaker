package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    single<SettingsInteractor> { SettingsInteractorImpl(get()) }

    viewModel { SettingsViewModel(get()) }
}