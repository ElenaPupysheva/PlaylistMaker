package com.practicum.playlistmaker.search.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.CLICK_DEBOUNCE_DELAY
import com.practicum.playlistmaker.domain.models.EXTRA_TRACK
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.presentation.SearchUiState
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.ui.TracklistCompose
import com.practicum.playlistmaker.ui.theme.YsFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCompose(viewModel: SearchViewModel) {
    val state by viewModel.uiState.observeAsState(SearchUiState())
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var clickEnabled by remember { mutableStateOf(true) }
    val openTrack: (Track) -> Unit = remember {
        { track ->
            if (clickEnabled) {
                clickEnabled = false

                viewModel.onTrackClick(track)
                ctx.startActivity(
                    Intent(ctx, PlayerActivity::class.java)
                        .putExtra(EXTRA_TRACK, Gson().toJson(track))
                )

                scope.launch {
                    delay(CLICK_DEBOUNCE_DELAY)
                    clickEnabled = true
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Text(
                        text = stringResource(R.string.search),
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = YsFontFamily),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
            // Search field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.size_52dp))
                    .padding(horizontal = dimensionResource(id = R.dimen.small_icon_pad)),
                contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.size_36dp))
                        .onFocusChanged { if (it.isFocused) viewModel.onFocusGained() },
                    value = state.stringValue,
                    onValueChange = viewModel::onTextChanged,
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.search)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search_bar_icon),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (state.stringValue.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.onTextChanged("")
                                viewModel.onFocusGained()
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.clear_search),
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.performSearch(state.stringValue) }
                    ),
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            Spacer(Modifier.height(dimensionResource(id = R.dimen.size_6dp)))

            Box(Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(dimensionResource(id = R.dimen.size_44dp))
                                .align(Alignment.TopCenter)
                                .padding(top = dimensionResource(id = R.dimen.size_140dp)),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    state.showHistory -> {
                        Column(Modifier.fillMaxSize()) {
                            Text(
                                text = stringResource(R.string.you_search),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(
                                    top = dimensionResource(id = R.dimen.us_padSt),
                                    bottom = 16.dp
                                )
                            ) {
                                itemsIndexed(
                                    state.historyList,
                                    key = { _, t -> t.trackId?.toString() ?: t.trackName.orEmpty() }
                                ) { i, track ->
                                    Surface(onClick = { openTrack(track) }, tonalElevation = 0.dp) {
                                        TracklistCompose(
                                            trackName = track.trackName.orEmpty(),
                                            artistName = track.artistName.orEmpty(),
                                            trackTime = track.trackTimeMillis.toMmSs(),
                                            imageUrl = track.artworkUrl100
                                        )
                                    }
                                    if (i < state.historyList.lastIndex) {
                                        Divider(
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    }
                                }
                            }
                            Button(
                                onClick = viewModel::clearHistory,
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 16.dp, bottom = 80.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                shape = MaterialTheme.shapes.large
                            ) { Text(stringResource(R.string.history_clear)) }
                        }
                    }

                    state.error != null -> {
                        ErrorWithRetry(
                            kind = state.error!!,
                            onRetry = {
                                val q = state.lastSearchQuery
                                if (q.isNotBlank()) viewModel.performSearch(q)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(top = 102.dp)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.us_padSt),
                                bottom = 16.dp
                            )
                        ) {
                            itemsIndexed(
                                state.trackList,
                                key = { _, t -> t.trackId?.toString() ?: t.trackName.orEmpty() }
                            ) { i, track ->
                                Surface(onClick = { openTrack(track) }, tonalElevation = 0.dp) {
                                    TracklistCompose(
                                        trackName = track.trackName.orEmpty(),
                                        artistName = track.artistName.orEmpty(),
                                        trackTime = track.trackTimeMillis.toMmSs(),
                                        imageUrl = track.artworkUrl100
                                    )
                                }
                                if (i < state.trackList.lastIndex) {
                                    Divider(
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorWithRetry(
    kind: SearchError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (iconRes, textRes, showRetry) = when (kind) {
        SearchError.NotFound -> Triple(R.drawable.error_search, R.string.nothing_found, false)
        SearchError.Network -> Triple(R.drawable.error_net_light, R.string.error_net, true)
    }

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (showRetry) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.large
            ) { Text(stringResource(R.string.error_refresh)) }
        }
    }
}

private fun Long?.toMmSs(): String {
    if (this == null || this <= 0L) return "00:00"
    val total = (this / 1000).toInt()
    return "%02d:%02d".format(total / 60, total % 60)
}
