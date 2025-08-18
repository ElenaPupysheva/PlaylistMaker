package com.practicum.playlistmaker.ui

import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R

@Composable
fun GlideCover(
    imageUrl: String?,
    modifier: Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply { scaleType = ImageView.ScaleType.CENTER_CROP }
        },
        update = { iv ->
            val url = imageUrl
            if (url.isNullOrBlank()) {
                iv.setImageResource(R.drawable.placeholder)
            } else {
                Glide.with(iv).clear(iv)
                Glide.with(iv)
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(iv)
            }
        }
    )
}
