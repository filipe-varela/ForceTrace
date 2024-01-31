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
                Color.White,
                2.dp.toPx()
            )
        }

    }
}