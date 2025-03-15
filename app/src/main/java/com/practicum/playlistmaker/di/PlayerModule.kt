package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.api.AudioPlayer
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.player.presentation.AndroidAudioPlayer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    single<AudioPlayer> { AndroidAudioPlayer() }

    single { PlayerInteractorImpl(get()) }

    viewModel { PlayerViewModel(get()) }
}