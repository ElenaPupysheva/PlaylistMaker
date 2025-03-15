package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.player.data.impl.AndroidAudioPlayer
import com.practicum.playlistmaker.player.domain.api.AudioRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    single { MediaPlayer() }

    single<AudioRepository> { AndroidAudioPlayer(get()) }

    single { PlayerInteractorImpl(get()) }

    viewModel { PlayerViewModel(get()) }
}