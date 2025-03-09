package com.practicum.playlistmaker.main.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> get() = _navigationEvent

    fun onSearchClicked() {
        _navigationEvent.value = NavigationEvent.OpenSearch
    }

    fun onMediaClicked() {
        _navigationEvent.value = NavigationEvent.OpenMedia
    }

    fun onSettingsClicked() {
        _navigationEvent.value = NavigationEvent.OpenSettings
    }
}

sealed class NavigationEvent {
    object OpenSearch : NavigationEvent()
    object OpenMedia : NavigationEvent()
    object OpenSettings : NavigationEvent()
}