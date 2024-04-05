package com.vilp.forcetrace

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vilp.forcetrace.model.ForcePoint
import com.vilp.forcetrace.ui.theme.ForceTraceTheme
import com.vilp.forcetrace.ui.widgets.BottomBar
import com.vilp.forcetrace.ui.widgets.DrawingArea
import com.vilp.forcetrace.ui.widgets.ErasingCanvas
import com.vilp.forcetrace.ui.widgets.OptionsButton
import com.vilp.forcetrace.ui.widgets.TrajectoriesCanvas
import com.vilp.forcetrace.ui.widgets.red2blue
import com.vilp.forcetrace.utils.ForceResult
import com.vilp.forcetrace.utils.resolve
import com.vilp.forcetrace.viewmodel.StylusState
import com.vilp.forcetrace.viewmodel.StylusViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : ComponentActivity() {
    private var stylusState: StylusState by mutableStateOf(StylusState())
    private val viewModel: StylusViewModel by viewModels()
    private val exportFileRegister = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { exportingCallback(it) }
    private var exportedFile: File? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
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
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        TrajectoriesCanvas(
                            nLines = 10, axisStrokePx = 4, modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                        DrawingArea(
                            stylusState = stylusState, modifier = Modifier.align(
                                Alignment.Center
                            )
                        ) {
                            return@DrawingArea if (!stylusState.erasingMode)
                                viewModel.processMotionEvent(it)
                            else false
                        }
                        val erasingRadius = with(LocalDensity.current) { 12.dp.toPx() }
                        if (stylusState.erasingMode) ErasingCanvas(
                            stylusState = stylusState,
                            erasingRadius = erasingRadius,
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        ) {
                            viewModel.processErasingEvent(
                                it,
                                erasingRadius
                            )
                        }
                        val totalSize: Float =
                            with(LocalDensity.current) { min(maxHeight, maxWidth).toPx() }
                        var fname: String by remember { mutableStateOf("default") }
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
                                if (stylusState.points.isNotEmpty()) {
                                    val pointsAsCSV: String = buildCSVContent(
                                        stylusState.points,
                                        totalSize
                                    )
                                    exportCSV(
                                        fname,
                                        pointsAsCSV
                                    ).resolve {
                                        when (it) {
                                            is ForceResult.Failure -> Toast.makeText(
                                                this@MainActivity,
                                                "Failed due ${it.error}",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            is ForceResult.Success -> shareFile(it.data)
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "There is no points to export",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        val colorBarWidth: Dp = with(LocalDensity.current) {
                            (max(maxHeight, maxWidth).toPx() - totalSize) / 4f
                        }.toInt().dp
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(colorBarWidth)
                                .padding(24.dp)
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(24.dp))
                        ) {
                            TextField(
                                value = fname,
                                onValueChange = { fname = it },
                                label = { Text("File Name") }
                            )
                        }
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(colorBarWidth)
                                .padding(24.dp)
                                .align(Alignment.CenterEnd)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.Gray)
                        ) {
                            val mappingForce: (Float) -> Dp = { f ->
                                // range the force values between [0, .5]
                                maxHeight * f
                            }
                            if (stylusState.isPressing) {
                                BoxWithConstraints(
                                    modifier = Modifier
                                        .height(mappingForce(stylusState.points.last().f))
                                        .fillMaxWidth()
                                        .align(Alignment.BottomEnd)
                                        .background(red2blue(stylusState.points.last().f))
                                ) {
                                    Text(
                                        "${stylusState.points.last().f}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White,
                                        modifier = Modifier.align(Alignment.TopCenter)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun buildCSVContent(points: List<ForcePoint>, totalSize: Float): String {
        return "t,pos_x,pos_y,force\n" + points.map {
            ForcePoint(
                it.x / totalSize,
                it.y / totalSize,
                it.f,
                it.t
            )
        }.joinToString("\n") {
            "${it.t},${it.x},${it.y},${it.f}"
        }
    }

    private fun exportCSV(filename: String, content: String): ForceResult<File> {
        val cacheDir = applicationContext.cacheDir
        val csvFile = File(cacheDir, "$filename.csv")
        return try {
            FileWriter(csvFile).use {
                it.write(content)
            }
            ForceResult.Success(csvFile)
        } catch (e: IOException) {
            ForceResult.Failure(e.printStackTrace().toString())
        }
    }

    private fun shareFile(file: File) {
        exportedFile = file
        val uri = FileProvider.getUriForFile(
            this@MainActivity,
            "$packageName.provider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        exportFileRegister.launch(
            Intent.createChooser(shareIntent, "Exporting trace")
        )
    }

    private fun exportingCallback(activityResult: ActivityResult) {
        Toast.makeText(
            this@MainActivity,
            if (activityResult.resultCode == RESULT_OK)
                "Exported!"
            else
                "Failed to export!",
            Toast.LENGTH_LONG
        ).show()
        exportedFile.let {
            if (it != null && !it.delete()) Toast.makeText(
                this@MainActivity,
                "Failed to delete file",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}


