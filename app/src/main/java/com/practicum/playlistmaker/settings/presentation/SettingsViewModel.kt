package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import kotlinx.coroutines.launch

class SettingsViewModel  (private val settingsInteractor: SettingsInteractor
) : ViewModel() {
    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> get() = _darkThemeEnabled

    init {
        loadThemeState()
    }

    private fun loadThemeState() {
        viewModelScope.launch {
            val isEnabled = settingsInteractor.isDarkThemeEnabled()
            _darkThemeEnabled.value = isEnabled
        }
    }

    fun onThemeToggled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsInteractor.setDarkThemeEnabled(isEnabled)
            _darkThemeEnabled.value = isEnabled
        }
    }
}