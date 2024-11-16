package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {
    val PRACTICUM_PREFERENCES = "practicum_preferences"
    val SWITCH_KEY = "dark_theme"
    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(PRACTICUM_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(SWITCH_KEY, false)

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        val sharedPrefs = getSharedPreferences("PRACTICUM_PREFERENCES", MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean("SWITCH_KEY", darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}