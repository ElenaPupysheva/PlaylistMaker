package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import com.practicum.playlistmaker.ui.theme.YsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCompose(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val app = context.applicationContext as App
    val isDarkTheme by viewModel.darkThemeEnabled.observeAsState(initial = app.darkTheme)

    LaunchedEffect(isDarkTheme) {
        if (app.darkTheme != isDarkTheme) app.switchTheme(isDarkTheme)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = YsFontFamily)
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(R.dimen.us_padSt),
                        end = dimensionResource(R.dimen.us_padEn)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dark_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.weight(1f)
                )
                SwitchWithCustomColors(
                    checked = isDarkTheme,
                    onCheckedChange = { viewModel.onThemeToggled(it) }
                )
            }

            SettingsRow(
                textResId = R.string.share,
                iconResId = R.drawable.share
            ) {
                val shareMessage = context.getString(R.string.link_course)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                }
                context.startActivity(
                    Intent.createChooser(
                        intent,
                        context.getString(R.string.share_text)
                    )
                )
            }

            SettingsRow(
                textResId = R.string.support,
                iconResId = R.drawable.support
            ) {
                val message = context.getString(R.string.message_text)
                val theme = context.getString(R.string.theme_text)
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.link_mail)))
                    putExtra(Intent.EXTRA_TEXT, message)
                    putExtra(Intent.EXTRA_SUBJECT, theme)
                }
                context.startActivity(intent)
            }

            SettingsRow(
                textResId = R.string.user_agree,
                iconResId = R.drawable.forward
            ) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.link_agreement))
                )
                context.startActivity(intent)
            }
        }
    }
}

@Composable
private fun SettingsRow(
    textResId: Int,
    iconResId: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.us_padSt),
                end = dimensionResource(R.dimen.us_padEn),
                top = dimensionResource(R.dimen.pad_vertical),
                bottom = dimensionResource(R.dimen.pad_vertical)
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(textResId),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.weight(1f)
        )
        iconResId?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SwitchWithCustomColors(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.onTertiary,
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
            uncheckedTrackColor = MaterialTheme.colorScheme.onTertiary
        )
    )
}
