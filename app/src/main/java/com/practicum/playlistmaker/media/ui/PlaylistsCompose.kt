package com.practicum.playlistmaker.media.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.practicum.playlistmaker.media.presentation.PlaylistsState
import com.practicum.playlistmaker.media.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.ui.ErrorStateCompose
import com.practicum.playlistmaker.ui.ErrorType

@Composable
fun PlaylistsCompose(
    viewModel: PlaylistsViewModel,
    onCreateNew: () -> Unit,
    onOpenDetails: (Long) -> Unit,
) {
    LaunchedEffect(Unit) { viewModel.getAllPlaylists() }

    val state = viewModel.observeState().observeAsState(PlaylistsState.Loading).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.main_padSt))
    ) {
        // Кнопка
        Button(
            onClick = onCreateNew,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = dimensionResource(R.dimen.top_mar)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.background
            ),
            shape = MaterialTheme.shapes.large
        ) { Text(stringResource(R.string.new_playlist)) }

        Spacer(Modifier.height(46.dp))

        val isLoading = state is PlaylistsState.Loading
        val isEmpty = state is PlaylistsState.Empty
        val isContent = state is PlaylistsState.Content

        // LOADING
        AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.size_44dp))
                        .padding(top = dimensionResource(R.dimen.size_140dp)),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        AnimatedVisibility(visible = isEmpty, enter = fadeIn(), exit = fadeOut()) {
            Box(Modifier.fillMaxSize()) {
                ErrorStateCompose(
                    type = ErrorType.NotFound,
                    message = stringResource(R.string.media_playlist),
                    centerVertically = false,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )
            }
        }

        // CONTENT
        AnimatedVisibility(visible = isContent, enter = fadeIn(), exit = fadeOut()) {
            val content = state as PlaylistsState.Content
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(top = 8.dp, bottom = 64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(content.playlists, key = { it.id }) { p ->
                    PlaylistCardCompose(
                        title = p.name,
                        trackCount = p.trackCount,
                        coverUrl = p.imagePath,
                        onClick = { onOpenDetails(p.id) }
                    )
                }
            }
        }
    }
}
