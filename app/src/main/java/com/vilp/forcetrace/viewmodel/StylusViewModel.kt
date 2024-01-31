package com.vilp.forcetrace.viewmodel

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.vilp.forcetrace.model.DataPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class StylusViewModel : ViewModel() {
    private var dataPoints = mutableListOf<DataPoint>()

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
                dataPoints.add(DataPoint(event.x, event.y))
            }

            MotionEvent.ACTION_MOVE -> {
                dataPoints.add(DataPoint(event.x, event.y))
            }

            MotionEvent.ACTION_UP -> {
                dataPoints.add(DataPoint(event.x, event.y))
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
                    points = dataPoints.map { Offset(it.x,it.y) }
                )
            )
        }

        return true
    }

    private fun cancelLastStroke() {}
}