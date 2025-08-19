package com.practicum.playlistmaker.search.ui

import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
import com.practicum.playlistmaker.ui.ErrorStateCompose
import com.practicum.playlistmaker.ui.ErrorType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCompose(viewModel: SearchViewModel) {
    val state by viewModel.uiState.observeAsState(SearchUiState())
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()
    val fieldContainer = if (isDark) Color.White else Color(0xFFE6E8EB)
    val placeholderColor =
        if (isDark) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        else Color(0xFFAEAFB4)
    val iconTint =
        if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFAEAFB4)
    val inputTextColor = Color(0xFF1A1B22)

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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
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
            // Поле поиска
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.size_52dp))
                    .padding(horizontal = dimensionResource(id = R.dimen.small_icon_pad)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = fieldContainer,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {}
                TextField(
                    value = state.stringValue,
                    onValueChange = viewModel::onTextChanged,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (it.isFocused) viewModel.onFocusGained() },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search),
                            style = MaterialTheme.typography.bodyLarge,
                            color = placeholderColor,
                            maxLines = 1
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painterResource(R.drawable.search_bar_icon),
                            null,
                            tint = iconTint
                        )
                    },
                    trailingIcon = {
                        if (state.stringValue.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.onTextChanged("")
                                viewModel.onFocusGained()
                            }) {
                                Icon(
                                    painterResource(R.drawable.clear_search),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { viewModel.performSearch(state.stringValue) }),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = inputTextColor),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = inputTextColor,
                        unfocusedTextColor = inputTextColor,
                        focusedPlaceholderColor = placeholderColor,
                        unfocusedPlaceholderColor = placeholderColor,
                        focusedLeadingIconColor = iconTint,
                        unfocusedLeadingIconColor = iconTint,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(Modifier.height(dimensionResource(id = R.dimen.size_6dp)))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(
                                    top = dimensionResource(id = R.dimen.us_padSt),
                                    bottom = 16.dp
                                )
                            ) {
                                itemsIndexed(
                                    state.historyList,
                                    key = { _, t -> t.trackId?.toString() ?: t.trackName.orEmpty() }
                                ) { _, track ->
                                    Surface(onClick = { openTrack(track) }, tonalElevation = 0.dp) {
                                        TracklistCompose(
                                            trackName = track.trackName.orEmpty(),
                                            artistName = track.artistName.orEmpty(),
                                            trackTime = track.trackTimeMillis.toMmSs(),
                                            imageUrl = track.artworkUrl100
                                        )
                                    }
                                }
                            }
                            Button(
                                onClick = viewModel::clearHistory,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .width(148.dp)
                                    .height(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onSecondary,
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),
                                shape = MaterialTheme.shapes.large,
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    stringResource(R.string.history_clear),
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    state.error != null -> {
                        val errorType = when (state.error) {
                            SearchError.NotFound -> ErrorType.NotFound
                            SearchError.Network -> ErrorType.Network
                            else -> error("Unsupported SearchError: ${state.error}")
                        }
                        ErrorStateCompose(
                            type = errorType,
                            message = when (errorType) {
                                ErrorType.NotFound -> stringResource(R.string.nothing_found)
                                ErrorType.Network -> stringResource(R.string.error_net)
                            },
                            onRetry = if (errorType == ErrorType.Network) {
                                {
                                    val q = state.lastSearchQuery
                                    if (q.isNotBlank()) viewModel.performSearch(q)
                                }
                            } else null,
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
                            ) { _, track ->
                                Surface(onClick = { openTrack(track) }, tonalElevation = 0.dp) {
                                    TracklistCompose(
                                        trackName = track.trackName.orEmpty(),
                                        artistName = track.artistName.orEmpty(),
                                        trackTime = track.trackTimeMillis.toMmSs(),
                                        imageUrl = track.artworkUrl100
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

private fun Long?.toMmSs(): String {
    if (this == null || this <= 0L) return "00:00"
    val total = (this / 1000).toInt()
    return "%02d:%02d".format(total / 60, total % 60)
}
