package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.ui.TracklistCompose
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.presentation.FavoriteState
import com.practicum.playlistmaker.media.presentation.FavoritesViewModel

@Composable
fun FavoritesCompose(
    viewModel: FavoritesViewModel,
    onOpenPlayer: (Track) -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadFavorites() }

    val state by viewModel.state.observeAsState(FavoriteState.Loading)
    val triggerTrack by viewModel.onTrackClickTrigger.observeAsState()

    when (val s = state) {
        is FavoriteState.Loading -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is FavoriteState.Empty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.media_clean),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        is FavoriteState.Content -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(items = s.tracks, key = { it.trackId }) { t ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.clickDebounce(t) }
                            .padding(vertical = 2.dp)
                    ) {
                        TracklistCompose(
                            trackName = t.trackName,
                            artistName = t.artistName,
                            trackTime = (t.trackTimeMillis / 1000L).let { sec ->
                                "%02d:%02d".format((sec / 60).toInt(), (sec % 60).toInt())
                            },
                            imageUrl = t.artworkUrl100
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
