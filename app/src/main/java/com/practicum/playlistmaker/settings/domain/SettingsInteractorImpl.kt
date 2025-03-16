package com.practicum.playlistmaker.settings.domain

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return settingsRepository.isDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        settingsRepository.setDarkThemeEnabled(enabled)
    }
}