package com.vilp.forcetrace.viewmodel

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.vilp.forcetrace.model.DataPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class StylusViewModel : ViewModel() {
    private var dataPoints = HistoryState(mutableListOf<DataPoint>())
    private var tmpPoints = mutableListOf<DataPoint>()

    private var _stylusState = MutableStateFlow(StylusState())
    val stylusState: StateFlow<StylusState> = _stylusState

    private fun requestRendering(stylusState: StylusState) {
        _stylusState.update {
            return@update stylusState
        }
    }

    fun processMotionEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                tmpPoints.add(DataPoint(event.x, event.y))
            }

            MotionEvent.ACTION_MOVE -> {
                tmpPoints.add(DataPoint(event.x, event.y))
            }

            MotionEvent.ACTION_UP -> {
                tmpPoints.add(DataPoint(event.x, event.y))
                dataPoints.add(tmpPoints.toMutableList())
                tmpPoints.clear()
            }

            MotionEvent.ACTION_CANCEL -> {
                cancelLastStroke()
            }

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

    // TODO: Add operation for redo, undo, delete, clear, totalClear, export

    private fun buildPoints(): List<Offset> {
        val points = mutableListOf<Offset>()
        dataPoints.current.forEach {
            points.add(Offset(it.x,it.y))
        }
        tmpPoints.forEach {
            points.add(Offset(it.x,it.y))
        }
        return points
    }

    private fun cancelLastStroke() {}
}