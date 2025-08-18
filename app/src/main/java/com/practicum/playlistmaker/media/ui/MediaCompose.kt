package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.presentation.FavoritesViewModel
import com.practicum.playlistmaker.ui.theme.YsFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediaCompose(
    viewModel: FavoritesViewModel,
    onOpenPlayer: (Track) -> Unit,
    loadPlaylists: suspend () -> List<Playlist>,
    onOpenDetails: (Long) -> Unit,
    onCreateNewPlaylist: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })

    TopAppBar(
        modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.mar_26dp)),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onSecondary
        ),
        title = {
            Text(
                text = stringResource(R.string.media),
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = YsFontFamily)
            )
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage, modifier = Modifier.fillMaxWidth()) {
            listOf(
                stringResource(R.string.favorites),
                stringResource(R.string.playlists)
            ).forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            when (page) {
                0 -> FavoritesCompose(viewModel = viewModel, onOpenPlayer = onOpenPlayer)
                1 -> PlaylistsCompose(
                    loadPlaylists = loadPlaylists,
                    onCreateNew = onCreateNewPlaylist,
                    onOpenDetails = onOpenDetails
                )
            }
        }
    }
}
