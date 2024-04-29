//   Copyright 2024 Filipe André Varela
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import com.vilp.forcetrace.viewmodel.StylusState

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun ErasingCanvas(
    modifier: Modifier = Modifier,
    stylusState: StylusState,
    erasingRadius: Float,
    onEvent: (MotionEvent) -> Boolean
) {
    Canvas(
        modifier = modifier
            .clipToBounds()
            .aspectRatio(
                1f,
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            )
            .fillMaxSize()
            .pointerInteropFilter { onEvent(it) }
    ) {
        with(stylusState.lastPosition) {
            drawCircle(
                Color.White,
                erasingRadius,
                Offset(x, y)
            )
        }
    }
}