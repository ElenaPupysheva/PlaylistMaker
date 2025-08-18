package com.practicum.playlistmaker.media.ui

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.presentation.PlaylistsState
import com.practicum.playlistmaker.media.presentation.PlaylistsViewModel

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
        ) { Text(text = stringResource(R.string.new_playlist)) }

        Spacer(Modifier.height(dimensionResource(R.dimen.main_padSt)))

        when (state) {
            PlaylistsState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.size_44dp))
                            .padding(top = dimensionResource(R.dimen.size_140dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            PlaylistsState.Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.error_search),
                        contentDescription = null
                    )
                    Spacer(Modifier.height(dimensionResource(R.dimen.us_padSt)))
                    Text(
                        text = stringResource(R.string.media_playlist),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            is PlaylistsState.Content -> {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 64.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.playlists, key = { it.id }) { p ->
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
}
