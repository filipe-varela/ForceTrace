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

class HistoryState<T>(
    value: T,
    private val past: MutableList<T> = mutableListOf(),
    private val future: MutableList<T> = mutableListOf(),
) {
    var current = value
        private set


    fun add(newElement: T) {
        past.add(current)
        current = newElement
        if (future.isNotEmpty()) future.clear()
    }

    fun redo() {
        if (future.isEmpty()) return
        past.add(current)
        current = future.removeFirst()
    }

    fun undo() {
        if (past.isEmpty()) return
        future.add(0, current)
        current = past.removeLast()
    }

    fun canRedo() = future.isNotEmpty()

    fun canUndo() = past.isNotEmpty()
}