package com.practicum.playlistmaker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R

enum class ErrorType { NotFound, Network }

@Composable
fun ErrorStateCompose(
    type: ErrorType,
    modifier: Modifier = Modifier,
    message: String = "",
    onRetry: (() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()

    val (iconRes, defaultText, showRetry) = when (type) {
        ErrorType.NotFound -> Triple(
            if (isDark) R.drawable.error_search_night else R.drawable.error_search,
            stringResource(R.string.nothing_found),
            false
        )

        ErrorType.Network -> Triple(
            if (isDark) R.drawable.error_net_night else R.drawable.error_net_light,
            stringResource(R.string.error_net),
            true
        )
    }

    val textToShow = if (message.isNotBlank()) message else defaultText

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = textToShow,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (showRetry && onRetry != null) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text(text = stringResource(R.string.error_refresh))
            }
        }
    }
}
