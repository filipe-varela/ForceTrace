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

import com.vilp.forcetrace.model.DataPoint
import com.vilp.forcetrace.model.ForcePoint

data class StylusState(
    var lastPosition: DataPoint = DataPoint(-100f, -100f),
    var points: List<ForcePoint> = mutableListOf(),
    var erasingMode: Boolean = false,
    var isPressing: Boolean = false
)