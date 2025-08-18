package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.presentation.FavoriteState
import com.practicum.playlistmaker.media.presentation.FavoritesViewModel
import com.practicum.playlistmaker.ui.TracklistCompose

@Composable
fun FavoritesCompose(
    viewModel: FavoritesViewModel,
    onOpenPlayer: (Track) -> Unit
) {
    // загрузка при первом показе
    LaunchedEffect(Unit) { viewModel.loadFavorites() }

    val state = viewModel.state.observeAsState(FavoriteState.Loading).value
    val triggerTrack = viewModel.onTrackClickTrigger.observeAsState().value

    when (state) {
        FavoriteState.Loading -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.size_44dp))
                        .align(Alignment.TopCenter)
                        .padding(top = dimensionResource(R.dimen.size_140dp)),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        FavoriteState.Empty -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 106.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.error_search),
                    contentDescription = null
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.us_padSt)))
                Text(
                    text = stringResource(R.string.media_clean),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        is FavoriteState.Content -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = dimensionResource(R.dimen.us_padSt)),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(
                    items = state.tracks,
                    key = { _, t -> t.trackId?.toString() ?: t.trackName.orEmpty() }
                ) { i, t ->
                    Surface(onClick = { viewModel.clickDebounce(t) }, tonalElevation = 0.dp) {
                        TracklistCompose(
                            trackName = t.trackName.orEmpty(),
                            artistName = t.artistName.orEmpty(),
                            trackTime = t.trackTimeMillis.toMmSs(),
                            imageUrl = t.artworkUrl100
                        )
                    }
                    if (i < state.tracks.lastIndex) {
                        Divider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(triggerTrack) {
        triggerTrack?.let { onOpenPlayer(it) }
    }
}

private fun Long?.toMmSs(): String {
    if (this == null || this <= 0L) return "00:00"
    val totalSec = (this / 1000).toInt()
    return "%02d:%02d".format(totalSec / 60, totalSec % 60)
}
