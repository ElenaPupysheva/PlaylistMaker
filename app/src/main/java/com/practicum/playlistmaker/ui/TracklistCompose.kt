package com.practicum.playlistmaker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R

@Composable
fun TracklistCompose(
    trackName: String = "Название песни",
    artistName: String = "Исполнитель",
    trackTime: String = "00:00",
    imageUrl: String?
) {
    Row(
        modifier = Modifier
            .height(61.dp)
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(R.dimen.small_icon_pad),
                horizontal = dimensionResource(R.dimen.size_13dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!imageUrl.isNullOrBlank()) {
            GlideCover(
                imageUrl = imageUrl,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.size_45dp))
                    .clip(RoundedCornerShape(2.dp))
            )
        } else {
            Image(
                painter = painterResource(R.drawable.placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.size_45dp))
                    .clip(RoundedCornerShape(2.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(R.dimen.small_icon_pad))
        ) {
            Text(
                text = trackName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.size_6dp))
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(R.drawable.ic),
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.size_13dp))
                )
                Text(
                    text = trackTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.forward_track),
            contentDescription = null,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.us_padEn))
        )
    }
}
