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

package com.vilp.forcetrace.viewmodel

import android.os.Build
import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vilp.forcetrace.model.DataPoint
import com.vilp.forcetrace.model.ForcePoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.jvm.internal.Ref.FloatRef
import kotlin.math.pow

class StylusViewModel : ViewModel() {
    private var totalSize: Float = 1f
    private var dataPoints = HistoryState(mutableListOf<ForcePoint>())
    private var tmpPoints = mutableListOf<ForcePoint>()

    private var _stylusState = MutableStateFlow(StylusState())
    val stylusState: StateFlow<StylusState> = _stylusState

    private val removeBrush = DataPoint(-100f, -100f)
    private var t0 = 0L

//    private val damping = 1000

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2500)
            _isLoading.value = false
        }
    }

    private fun requestRendering(stylusState: StylusState) {
        _stylusState.update {
            return@update stylusState
        }
    }

    fun processMotionEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                t0 = System.currentTimeMillis()
                tmpPoints.add(
                    ForcePoint(
                        event.x,
                        event.y,
                        event.pressure,
                        0L
                    )
                )
            }

            MotionEvent.ACTION_MOVE -> {
                val dt = System.currentTimeMillis() - t0
//                val previousForce = tmpPoints.last().f
                val currentForce = event.pressure
                tmpPoints.add(
                    ForcePoint(
                        event.x,
                        event.y,
                        currentForce,
                        dt
                    )
                )
            }

            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> performActionUp(event)
            MotionEvent.ACTION_CANCEL -> cancelLastStroke()
            else -> return false
        }

        requestRendering(
            StylusState(
                points = buildPoints(),
                isPressing = event.actionMasked == MotionEvent.ACTION_MOVE
            )
        )

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
        it: ForcePoint,
        event: MotionEvent
    ) = ((it.x - event.x).pow(2f) + (it.y - event.y).pow(2f)).pow(.5f)

    private fun performActionUp(event: MotionEvent) {
        val canceled =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (event.flags and MotionEvent.FLAG_CANCELED) == MotionEvent.FLAG_CANCELED
        if (canceled) {
            cancelLastStroke()
        } else {
            tmpPoints.add(
                ForcePoint(
                    event.x,
                    event.y,
                    event.pressure,
                    System.currentTimeMillis() - t0
                )
            )
            val totalPoints = dataPoints.current.toMutableList()
            totalPoints.addAll(tmpPoints)
            dataPoints.add(totalPoints)
            tmpPoints.clear()
        }
    }

    private fun buildPoints(): List<ForcePoint> {
        val points = mutableListOf<ForcePoint>()
        points.addAll(dataPoints.current.toList())
        if (tmpPoints.isNotEmpty()) tmpPoints.forEach { points.add(it) }
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

    fun exportingPointsAsCSV(): String {
        return "t,pos_x,pos_y,force\n" + dataPoints.current.map {
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

    fun importingPointsFromCSV(points: List<List<Float>>) {
        clearPoints()
        dataPoints.add(points.map { point -> ForcePoint(
            t = point[0].toLong(),
            x = (point[1] + 1) / 2f * totalSize,
            y = totalSize - (point[2] + 1) / 2f * totalSize,
            f = point[3]
        )}.toMutableList())
        updatePoints()
    }

    fun updateTotalSize(ts: Float) { totalSize = ts }
}