package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}