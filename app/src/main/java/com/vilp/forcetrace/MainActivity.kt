package com.vilp.forcetrace

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vilp.forcetrace.ui.theme.ForceTraceTheme
import com.vilp.forcetrace.ui.widgets.TrajectoriesCanvas
import com.vilp.forcetrace.viewmodel.StylusState
import com.vilp.forcetrace.viewmodel.StylusViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var stylusState: StylusState by mutableStateOf(StylusState())
    private val viewModel: StylusViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stylusState.onEach { stylusState = it }.collect()
            }
        }
        setContent {
            ForceTraceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TrajectoriesCanvas(nLines = 10, axisStrokePx = 4)
                    DrawingArea(stylusState = stylusState)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text(text = "Algo 1")
                            Text(text = "Algo 2", modifier = Modifier.padding(start = 8.dp))
                            Text(text = "Algo 3", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun DrawingArea(
        modifier: Modifier = Modifier,
        stylusState: StylusState
    ) {
        Canvas(modifier = modifier
            .clipToBounds()
            .aspectRatio(
                1f,
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            )
            .fillMaxSize()
            .pointerInteropFilter { viewModel.processMotionEvent(it) }) {
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
}
