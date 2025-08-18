package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.ui.TracklistCompose
import android.content.Intent
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.practicum.playlistmaker.ui.theme.YsFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCompose(viewModel: SearchViewModel) {
    val state by viewModel.uiState.observeAsState(SearchUiState())
    val ctx = LocalContext.current
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val focusManager = LocalFocusManager.current
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.mar_26dp)),
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                        Icon(painter = painterResource(R.drawable.back), contentDescription = null)
                    }
                },
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
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.small_icon_pad)),
                value = state.stringValue,
                onValueChange = { viewModel.onTextChanged(it) },
                singleLine = true,
                placeholder = { Text(stringResource(R.string.search)) },
                trailingIcon = {
                    if (state.stringValue.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.onTextChanged("")
                            viewModel.onFocusGained()
                            focusManager.clearFocus()
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
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.performSearch(state.stringValue)
                    }
                )
            )

            Spacer(Modifier.height(8.dp))

            Box(Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.TopCenter)
                                .padding(top = 140.dp)
                        )
                    }

                    state.showHistory -> {
                        Column(Modifier.fillMaxSize()) {
                            Text(
                                text = stringResource(R.string.you_search),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(
                                    top = dimensionResource(id = R.dimen.us_padSt),
                                    bottom = WindowInsets.navigationBars
                                        .asPaddingValues().calculateBottomPadding()
                                )
                            ) {
                                itemsIndexed(
                                    items = state.historyList,
                                    key = { _, t -> t.trackId?.toString() ?: t.trackName.orEmpty() }
                                ) { index, track ->
                                    TrackItem(track = track, onClick = { openTrack(track) })
                                    if (index < state.historyList.lastIndex) {
                                        Divider(
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    }
                                }
                            }
                            Button(
                                onClick = { viewModel.clearHistory() },
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                            ) { Text(stringResource(R.string.history_clear)) }
                        }
                    }

                    state.isError -> {
                        ErrorWithRetry(
                            text = stringResource(R.string.nothing_found),
                            onRetry = {
                                val last = state.lastSearchQuery
                                if (last.isNotBlank()) viewModel.performSearch(last)
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
                                bottom = WindowInsets.navigationBars
                                    .asPaddingValues().calculateBottomPadding()
                            )
                        ) {
                            itemsIndexed(
                                items = state.trackList,
                                key = { _, t -> t.trackId?.toString() ?: t.trackName.orEmpty() }
                            ) { index, track ->
                                TrackItem(track = track, onClick = { openTrack(track) })
                                if (index < state.trackList.lastIndex) {
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
    text: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    iconRes: Int = R.drawable.error_search,
    buttonTextRes: Int = R.string.error_refresh
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painter = painterResource(iconRes), contentDescription = null)
        Spacer(Modifier.height(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRetry) { Text(text = stringResource(buttonTextRes)) }
    }
}

@Composable
fun TrackItem(track: Track, onClick: () -> Unit) {
    Surface(onClick = onClick, tonalElevation = 0.dp) {
        TracklistCompose(
            trackName = track.trackName.orEmpty(),
            artistName = track.artistName.orEmpty(),
            trackTime = track.trackTimeMillis.toMmSs(),
            imageUrl = track.artworkUrl100
        )
    }
}

fun Long?.toMmSs(): String {
    if (this == null || this <= 0L) return "00:00"
    val total = (this / 1000).toInt()
    return "%02d:%02d".format(total / 60, total % 60)
}