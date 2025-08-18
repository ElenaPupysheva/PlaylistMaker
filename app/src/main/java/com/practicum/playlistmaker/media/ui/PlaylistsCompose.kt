package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.launch

@Composable
fun PlaylistsCompose(
    loadPlaylists: suspend () -> List<Playlist>,
    onCreateNew: () -> Unit,
    onOpenDetails: (Long) -> Unit
) {
    var loading by remember { mutableStateOf(true) }
    var playlists by remember { mutableStateOf<List<Playlist>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        playlists = loadPlaylists()
        loading = false
    }

    // аналог onResume()
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    loading = true
                    playlists = loadPlaylists()
                    loading = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = onCreateNew,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text(text = stringResource(R.string.new_playlist))
        }

        Spacer(Modifier.height(16.dp))

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            playlists.isEmpty() -> EmptyPlaylistsPlaceholder()
            else -> PlaylistsGrid(playlists = playlists, onOpenDetails = onOpenDetails)
        }
    }
}

@Composable
private fun PlaylistsGrid(
    playlists: List<Playlist>,
    onOpenDetails: (Long) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 64.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ВАЖНО: grid-версия items
        items(items = playlists, key = { it.id }) { p ->
            PlaylistCardCompose(
                title = p.name,
                trackCount = "${p.trackCount} tracks",
                onClick = { onOpenDetails(p.id) }
            )
        }
    }
}

@Composable
private fun EmptyPlaylistsPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 106.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(R.drawable.placeholder), contentDescription = null)
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.media_playlist),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
