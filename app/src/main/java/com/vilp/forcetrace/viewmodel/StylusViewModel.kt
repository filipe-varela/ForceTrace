package com.vilp.forcetrace.viewmodel

import android.os.Build
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.vilp.forcetrace.model.DataPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

class StylusViewModel : ViewModel() {
    private var dataPoints = HistoryState(mutableListOf<DataPoint>())
    private var tmpPoints = mutableListOf<DataPoint>()

    private var _stylusState = MutableStateFlow(StylusState())
    val stylusState: StateFlow<StylusState> = _stylusState

    private val removeBrush = DataPoint(-100f, -100f)

    private fun requestRendering(stylusState: StylusState) {
        _stylusState.update {
            return@update stylusState
        }
    }

    fun processMotionEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> tmpPoints.add(DataPoint(event.x, event.y))
            MotionEvent.ACTION_MOVE -> tmpPoints.add(DataPoint(event.x, event.y))
            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> performActionUp(event)
            MotionEvent.ACTION_CANCEL -> cancelLastStroke()
            else -> return false
        }

        with(event) {
            requestRendering(
                StylusState(
                    pressure,
                    orientation,
                    tilt = getAxisValue(MotionEvent.AXIS_TILT),
                    points = buildPoints()
                )
            )
        }

        return true
    }

    fun processErasingEvent(event: MotionEvent, radius: Float): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (dataPoints.current.isNotEmpty()) {
                    val cleanedPoints = dataPoints.current.filter {
                        computeDistance(it, event) > radius
                    }.toMutableList()
                    if (cleanedPoints.size != dataPoints.current.size) dataPoints.add(cleanedPoints)
                    requestRendering(
                        stylusState.value.copy(
                            lastPosition = DataPoint(event.x, event.y),
                            points = buildPoints()
                        )
                    )
                } else {
                    requestRendering(
                        stylusState.value.copy(
                            lastPosition = DataPoint(event.x, event.y)
                        )
                    )
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                requestRendering(stylusState.value.copy(lastPosition = removeBrush))
            }

            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                requestRendering(stylusState.value.copy(lastPosition = removeBrush))
            }

            else -> return false
        }

        return true
    }

    private fun computeDistance(
        it: DataPoint,
        event: MotionEvent
    ) = ((it.x - event.x).pow(2f) + (it.y - event.y).pow(2f)).pow(.5f)

    private fun performActionUp(event: MotionEvent) {
        val canceled =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (event.flags and MotionEvent.FLAG_CANCELED) == MotionEvent.FLAG_CANCELED
        if (canceled) {
            cancelLastStroke()
        } else {
            tmpPoints.add(DataPoint(event.x, event.y))
            val totalPoints = dataPoints.current.toMutableList()
            totalPoints.addAll(tmpPoints)
            dataPoints.add(totalPoints)
            tmpPoints.clear()
        }
    }

    // TODO: Add operation for delete, export

    private fun buildPoints(): List<Offset> {
        val points = mutableListOf<Offset>()
        dataPoints.current.forEach {
            points.add(Offset(it.x, it.y))
        }
        tmpPoints.forEach {
            points.add(Offset(it.x, it.y))
        }
        return points
    }

    fun undoPoints() {
        if (dataPoints.canUndo()) {
            dataPoints.undo()
            updatePoints()
        }
    }

    fun redoPoints() {
        if (dataPoints.canRedo()) {
            dataPoints.redo()
            updatePoints()
        }
    }

    fun clearPoints() {
        if (dataPoints.current.isNotEmpty()) {
            dataPoints.add(mutableListOf())
            updatePoints()
        }
    }

    private fun updatePoints() {
        requestRendering(
            stylusState.value.copy(
                points = buildPoints()
            )
        )
    }

    private fun cancelLastStroke() {
        tmpPoints.clear()
    }

    fun switchErasingModes() {
        requestRendering(stylusState.value.copy(erasingMode = !stylusState.value.erasingMode))
    }
}