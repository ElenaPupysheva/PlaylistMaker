package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor


class SettingsViewModel  (private val settingsInteractor: SettingsInteractor
) : ViewModel() {
    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> get() = _darkThemeEnabled

    init {
        loadThemeState()
    }

    private fun loadThemeState() {
            val isEnabled = settingsInteractor.isDarkThemeEnabled()
        _darkThemeEnabled.postValue(isEnabled)
    }

    fun onThemeToggled(isEnabled: Boolean) {
            settingsInteractor.setDarkThemeEnabled(isEnabled)
            _darkThemeEnabled.value = isEnabled
    }
}