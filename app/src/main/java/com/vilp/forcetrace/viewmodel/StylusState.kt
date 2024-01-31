package com.vilp.forcetrace.viewmodel

import androidx.compose.ui.geometry.Offset

data class StylusState(
    var pressure: Float = 0f,
    var orientation: Float = 0f,
    var tilt: Float = 0f,
    var points: List<Offset> = mutableListOf()
)