package com.practicum.playlistmaker.settings.ui

import android.R.attr.textStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.ui.theme.YsFontFamily
import com.practicum.playlistmaker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCompose() {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(bottom = dimensionResource(id = R.dimen.mar_26dp)),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                ),
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = YsFontFamily,
                        )
                    )
                }
            )

        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = MaterialTheme.colorScheme.onPrimary)
                    .padding(
                        start = dimensionResource(id = R.dimen.us_padSt),
                        end = dimensionResource(id = R.dimen.us_padSt)
                    )
            ) {
                darkThemeCustom()
                Spacer(modifier = Modifier.width(8.dp))
                MaterialText(R.string.share, R.drawable.share)
                Spacer(modifier = Modifier.width(8.dp))
                MaterialText(R.string.support, R.drawable.support)
                Spacer(modifier = Modifier.width(8.dp))
                MaterialText(R.string.user_agree, R.drawable.forward)
                }
        }
    )
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun SettingsComposePreview() {
    PlaylistMakerTheme {
        SettingsCompose()
    }
}

@Composable
fun MaterialText(
    textResId: Int,
    imgSrcId: Int? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = dimensionResource(id = R.dimen.us_padSt)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = textResId),
            modifier = Modifier.weight(1f)
        )

        imgSrcId?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SwitchWithCustomColors() {
    var checked by remember { mutableStateOf(true) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.onTertiary,
            uncheckedThumbColor = MaterialTheme.colorScheme.surface,
            uncheckedTrackColor = MaterialTheme.colorScheme.tertiary,
        )
    )
}

@Composable
fun darkThemeCustom(
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.dark_theme),
            modifier = Modifier.weight(1f)
        )
        SwitchWithCustomColors()
    }
}
