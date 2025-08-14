package com.practicum.playlistmaker.settings.ui

import android.R.attr.checked
import android.R.attr.enabled
import android.R.attr.textStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.us_padSt))
                )
                {

                }
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
