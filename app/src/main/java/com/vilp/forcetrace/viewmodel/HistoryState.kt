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