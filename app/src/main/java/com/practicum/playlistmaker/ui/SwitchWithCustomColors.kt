package com.practicum.playlistmaker.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R

@Composable
fun SwitchWithCustomColors(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    trackWidth: Dp = 32.dp,
    trackHeight: Dp = 12.dp,
    knobSize: Dp = 18.dp,
) {
    val trackColor = colorResource(R.color.ic_track)
    val knobColor = colorResource(R.color.ic_knob)

    val travel = trackWidth - knobSize
    val offsetX = animateDpAsState(
        targetValue = if (checked) travel else 0.dp,
        animationSpec = spring(stiffness = 600f),
        label = "switchOffset"
    ).value

    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch,
                interactionSource = interaction,
                indication = null
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .width(trackWidth)
                .height(knobSize)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(trackWidth)
                    .height(trackHeight)
                    .clip(RoundedCornerShape(trackHeight / 2))
                    .background(trackColor)
            )

            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .size(knobSize)
                    .clip(CircleShape)
                    .background(knobColor)
            )
        }
    }
}
