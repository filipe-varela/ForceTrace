package com.vilp.forcetrace.viewmodel

import androidx.compose.ui.geometry.Offset
import com.vilp.forcetrace.model.DataPoint

data class StylusState(
    var pressure: Float = 0f,
    var orientation: Float = 0f,
    var tilt: Float = 0f,
    var lastPosition: DataPoint = DataPoint(-100f, -100f),
    var points: List<Offset> = mutableListOf(),
    var erasingMode: Boolean = false
)