package com.practicum.playlistmaker.media.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.presentation.FavoriteState
import com.practicum.playlistmaker.media.presentation.FavoritesViewModel
import com.practicum.playlistmaker.ui.ErrorStateCompose
import com.practicum.playlistmaker.ui.ErrorType
import com.practicum.playlistmaker.ui.TracklistCompose

@Composable
fun FavoritesCompose(
    viewModel: FavoritesViewModel,
    onOpenPlayer: (Track) -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadFavorites() }

    val state = viewModel.state.observeAsState(FavoriteState.Loading).value
    val triggerTrack = viewModel.onTrackClickTrigger.observeAsState().value

    val isLoading = state is FavoriteState.Loading
    val isEmpty = state is FavoriteState.Empty
    val isContent = state is FavoriteState.Content

    // LOADING
    AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
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

    // EMPTY (106dp ниже табов)
    AnimatedVisibility(visible = isEmpty, enter = fadeIn(), exit = fadeOut()) {
        Box(Modifier.fillMaxSize()) {
            ErrorStateCompose(
                type = ErrorType.NotFound,
                message = stringResource(R.string.media_clean),
                centerVertically = false,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 106.dp)
            )
        }
    }

    // CONTENT
    AnimatedVisibility(visible = isContent, enter = fadeIn(), exit = fadeOut()) {
        val content = state as FavoriteState.Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = dimensionResource(R.dimen.us_padSt)),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(
                items = content.tracks,
                key = { _, t -> t.trackId?.toString() ?: t.trackName.orEmpty() }
            ) { _, t ->
                Surface(onClick = { viewModel.clickDebounce(t) }, tonalElevation = 0.dp) {
                    TracklistCompose(
                        trackName = t.trackName.orEmpty(),
                        artistName = t.artistName.orEmpty(),
                        trackTime = t.trackTimeMillis.toMmSs(),
                        imageUrl = t.artworkUrl100
                    )
                }
            }
        }
    }

    LaunchedEffect(triggerTrack) { triggerTrack?.let(onOpenPlayer) }
}

private fun Long?.toMmSs(): String {
    if (this == null || this <= 0L) return "00:00"
    val totalSec = (this / 1000).toInt()
    return "%02d:%02d".format(totalSec / 60, totalSec % 60)
}
