package com.practicum.playlistmaker.ui.tracks

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.domain.models.PRACTICUM_PREFERENCES
import com.practicum.playlistmaker.domain.models.SWITCH_KEY

class App : Application() {
    var darkTheme = false

    private lateinit var themePrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        themePrefs = getSharedPreferences(PRACTICUM_PREFERENCES, MODE_PRIVATE)

        Creator.init(getSharedPreferences("SEARCH_HISTORY", MODE_PRIVATE))

        if (!themePrefs.contains(SWITCH_KEY)) {
            val isSystemDarkTheme =
                (resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                        android.content.res.Configuration.UI_MODE_NIGHT_YES

            themePrefs.edit()
                .putBoolean(SWITCH_KEY, isSystemDarkTheme)
                .apply()
        }

        darkTheme = themePrefs.getBoolean(SWITCH_KEY, false)

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

        themePrefs.edit()
            .putBoolean(SWITCH_KEY, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
