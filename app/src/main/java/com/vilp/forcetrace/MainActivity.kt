package com.vilp.forcetrace

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vilp.forcetrace.ui.theme.ForceTraceTheme
import com.vilp.forcetrace.ui.widgets.BottomBar
import com.vilp.forcetrace.ui.widgets.DrawingArea
import com.vilp.forcetrace.ui.widgets.ErasingCanvas
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
                    val erasingRadius = with(LocalDensity.current) { 12.dp.toPx() }
                    if (stylusState.erasingMode) ErasingCanvas(
                        stylusState = stylusState,
                        erasingRadius = erasingRadius
                    ) {
                        viewModel.processErasingEvent(
                            it,
                            erasingRadius
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
                        val context = LocalContext.current
                        OptionsButton(id = R.drawable.baseline_download_24) {
                            if (stylusState.points.isNotEmpty()) {
                                val pointsAsCSV: String = "pos_x,pos_y,force\n" + stylusState.points
                                    .joinToString("\n") {
                                        "${it.x},${it.y},${it.f}"
                                    }
                                val exportData = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    putExtra(Intent.EXTRA_TITLE, "New trajectory")
                                    putExtra(Intent.EXTRA_TEXT, pointsAsCSV)
                                    type = "text/csv"
                                }
                                context.startActivity(
                                    Intent.createChooser(exportData, "Sharing trajectory")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


