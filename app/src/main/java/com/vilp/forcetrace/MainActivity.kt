package com.vilp.forcetrace

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
import androidx.compose.ui.Modifier
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
                    DrawingArea(stylusState = stylusState) { viewModel.processMotionEvent(it) }
                    BottomBar(horizontalAlignment = Alignment.BottomCenter) {
                        OptionsButton(id = R.drawable.baseline_design_services_24) {
                            println("Rubber")
                        }
                        OptionsButton(id = R.drawable.baseline_clear_24) {
                            println("Clear screen")
                        }
                        OptionsButton(id = R.drawable.baseline_undo_24) {
                            println("Undo")
                        }
                        OptionsButton(id = R.drawable.baseline_redo_24) {
                            println("Redo")
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
