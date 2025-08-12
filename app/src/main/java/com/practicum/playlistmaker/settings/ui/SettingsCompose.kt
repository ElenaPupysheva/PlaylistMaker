package com.practicum.playlistmaker.settings.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Small Top App Bar") }
            )
        }
    ) { innerPadding ->
        Text(
            text = "Настройки контент",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SettingsCompose() {
    SmallTopAppBarExample()
}

@Preview(showBackground = true, name = "Settings TopAppBar Preview")
@Composable
private fun SettingsComposePreview() {
    MaterialTheme {
        SettingsCompose()
    }
}
