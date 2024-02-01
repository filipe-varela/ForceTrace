package com.vilp.forcetrace.ui.widgets

import android.content.res.Configuration
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.vilp.forcetrace.viewmodel.StylusState
import kotlin.math.PI
import kotlin.math.cos

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawingArea(
    modifier: Modifier = Modifier,
    stylusState: StylusState,
    onEvent: (MotionEvent) -> Boolean
) {
    Canvas(modifier = modifier
        .clipToBounds()
        .aspectRatio(
            1f,
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        )
        .fillMaxSize()
        .pointerInteropFilter { onEvent(it) }) {
        with(stylusState) {
            drawPoints(
                points,
                PointMode.Points,
                red2blue(pressure),
                3.dp.toPx()
            )
        }
    }
}

fun red2blue(pressure: Float): Color {
    val a = listOf(0.5f, 0f, 0.5f)
    val d = listOf(0.0f, 0f, 0.5f)
    return Color(
        a[0] + a[0] * cos(2f * PI.toFloat() * (a[0] * pressure + d[0])),
        a[1] + a[1] * cos(2f * PI.toFloat() * (a[1] * pressure + d[1])),
        a[2] + a[2] * cos(2f * PI.toFloat() * (a[2] * pressure + d[2])),
    )
}