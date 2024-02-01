package com.vilp.forcetrace.viewmodel

import com.vilp.forcetrace.model.DataPoint
import com.vilp.forcetrace.model.ForcePoint

data class StylusState(
    var lastPosition: DataPoint = DataPoint(-100f, -100f),
    var points: List<ForcePoint> = mutableListOf(),
    var erasingMode: Boolean = false
)