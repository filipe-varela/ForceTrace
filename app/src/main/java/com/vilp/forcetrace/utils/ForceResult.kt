package com.vilp.forcetrace.utils

sealed class ForceResult<out T> {
    class Failure(val error: String) : ForceResult<Nothing>()
    class Success<T>(val data: T) : ForceResult<T>()
}

fun <T> ForceResult<T>.resolve(response: (ForceResult<T>) -> Unit) {
    response(this)
}