package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun PlaylistCardCompose(
    title: String = "Название плейлиста",
    trackCount: String = "0 tracks",
    imageRes: Int = R.drawable.placeholder
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .padding(bottom = 12.dp)
            .width(160.dp)
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Playlist cover",
                modifier = Modifier
                    .size(160.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

                )

            Text(
                text = trackCount,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PlaylistCardPreview() {
    MaterialTheme {
        PlaylistCardCompose()
    }
}