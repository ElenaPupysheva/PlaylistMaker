package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences

import com.practicum.playlistmaker.domain.models.PRACTICUM_PREFERENCES
import com.practicum.playlistmaker.domain.models.SWITCH_KEY
import com.practicum.playlistmaker.player.data.di.playerModule
import com.practicum.playlistmaker.search.data.di.dataModule
import com.practicum.playlistmaker.search.data.di.interactorModule
import com.practicum.playlistmaker.search.data.di.repositoryModule
import com.practicum.playlistmaker.search.data.di.viewModelModule
import com.practicum.playlistmaker.settings.data.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin


class App : Application(), KoinComponent {

    private lateinit var themePrefs: SharedPreferences

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                interactorModule,
                repositoryModule,
                viewModelModule,
                playerModule,
                settingsModule
            )
        }
        themePrefs = getSharedPreferences(PRACTICUM_PREFERENCES, MODE_PRIVATE)


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
