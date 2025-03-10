package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.models.SWITCH_KEY

class SettingsRepositoryImpl (
    private val sharedPreferences: SharedPreferences
    ) : SettingsRepository {

        override fun isDarkThemeEnabled(): Boolean {
            return sharedPreferences.getBoolean(SWITCH_KEY, false)
        }

        override fun setDarkThemeEnabled(enabled: Boolean) {
            sharedPreferences.edit()
                .putBoolean(SWITCH_KEY, enabled)
                .apply()
        }
    }
