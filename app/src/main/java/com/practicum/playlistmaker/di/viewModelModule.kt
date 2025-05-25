package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.presentation.FavoritesViewModel
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        FavoritesViewModel(favoritesInteractor = get())
    }
    viewModel {
        PlayerViewModel(
            playerInteractor = get(),
            favoritesInteractor = get(),
            playlistInteractor = get()
        )
    }

}
