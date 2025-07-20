package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.player.data.impl.AndroidAudioPlayer
import com.practicum.playlistmaker.player.domain.api.AudioRepository
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    single<PlayerInteractor> { PlayerInteractorImpl(get()) }

    viewModel<PlayerViewModel> {
        PlayerViewModel(get(), get(), get())
    }
}