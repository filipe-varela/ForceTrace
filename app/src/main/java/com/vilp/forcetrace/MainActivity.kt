package com.vilp.forcetrace

import android.content.res.Configuration
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vilp.forcetrace.ui.theme.ForceTraceTheme
import com.vilp.forcetrace.ui.widgets.BottomBar
import com.vilp.forcetrace.ui.widgets.DrawingArea
import com.vilp.forcetrace.ui.widgets.OptionsButton
import com.vilp.forcetrace.ui.widgets.TrajectoriesCanvas
import com.vilp.forcetrace.viewmodel.StylusState
import com.vilp.forcetrace.viewmodel.StylusViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var stylusState: StylusState by mutableStateOf(StylusState())
    private val viewModel: StylusViewModel by viewModels()

    @OptIn(ExperimentalComposeUiApi::class)
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
                    DrawingArea(stylusState = stylusState) {
                        return@DrawingArea if (!stylusState.erasingMode)
                            viewModel.processMotionEvent(it)
                        else false
                    }
                    if (stylusState.erasingMode) ErasingCanvas(stylusState = stylusState) {
                        viewModel.processErasingEvent(
                            it
                        )
                    }
                    BottomBar(horizontalAlignment = Alignment.BottomCenter) {
                        OptionsButton(id = R.drawable.baseline_design_services_24) {
                            viewModel.switchErasingModes()
                        }
                        OptionsButton(id = R.drawable.baseline_clear_24) {
                            viewModel.clearPoints()
                        }
                        OptionsButton(id = R.drawable.baseline_undo_24) {
                            viewModel.undoPoints()
                        }
                        OptionsButton(id = R.drawable.baseline_redo_24) {
                            viewModel.redoPoints()
                        }
                        OptionsButton(id = R.drawable.baseline_download_24) {
                            println("Export")
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun ErasingCanvas(
    modifier: Modifier = Modifier,
    stylusState: StylusState,
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
                12.dp.toPx(),
                Offset(x, y)
            )
        }
    }
}
