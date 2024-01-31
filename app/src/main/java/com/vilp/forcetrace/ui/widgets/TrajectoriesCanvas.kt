package com.vilp.forcetrace.ui.widgets

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vilp.forcetrace.ui.theme.ForceTraceTheme


@Composable
fun TrajectoriesCanvas(
    modifier: Modifier = Modifier,
    nLines: Int = 6,
    strokeWidthPx: Int = 1,
    axisStrokePx: Int = 2
) {
    Canvas(
        modifier = modifier
            .aspectRatio(1f, LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
            .fillMaxSize(),
    ) {
        drawGrid(nLines, strokeWidthPx.dp.toPx())
        drawAxis(axisStrokePx.dp.toPx())
    }
}

private fun DrawScope.drawAxis(axisStrokePx: Float) {
    drawLine(
        Color.Gray,
        start = Offset(size.width / 2f, 0f),
        end = Offset(size.width / 2f, size.height),
        strokeWidth = axisStrokePx
    )
    drawLine(
        Color.Gray,
        start = Offset(0f, size.height / 2f),
        end = Offset(size.width, size.height / 2f),
        strokeWidth = axisStrokePx
    )
}

private fun DrawScope.drawGrid(
    nLines: Int,
    strokeWidthPx: Float
) {
    val linesSize = size.width / (nLines + 1f)
    var startX: Float
    repeat(nLines + 2) {
        startX = linesSize * it
        drawLine(
            Color.LightGray,
            start = Offset(startX, 0f),
            end = Offset(startX, size.height),
            strokeWidth = strokeWidthPx
        )
        drawLine(
            Color.LightGray,
            start = Offset(0f, startX),
            end = Offset(size.width, startX),
            strokeWidth = strokeWidthPx
        )
    }
}

@Preview(name = "Trajectories Canvas", widthDp = 320)
@Composable
fun PreviewTrajectoriesCanvas() {
    ForceTraceTheme {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
        ) {
            TrajectoriesCanvas()
        }
    }
}
