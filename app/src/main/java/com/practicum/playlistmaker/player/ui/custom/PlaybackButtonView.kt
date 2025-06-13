package com.practicum.playlistmaker.player.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var playIcon: Bitmap? = null
    private var pauseIcon: Bitmap? = null

    var isPlaying: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private var clickListener: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playIcon = getDrawable(R.styleable.PlaybackButtonView_playIcon)?.toBitmap()
                pauseIcon = getDrawable(R.styleable.PlaybackButtonView_pauseIcon)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                clickListener?.invoke()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setOnPlaybackClickListener(listener: () -> Unit) {
        this.clickListener = listener
    }

    override fun onDraw(canvas: Canvas) {

        val iconToDraw = if (isPlaying) pauseIcon else playIcon
        iconToDraw?.let {
            val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            canvas.drawBitmap(it, null, rect, null)
        }
    }
}