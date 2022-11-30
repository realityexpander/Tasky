package com.realityexpander.tasky.core.util

import kotlinx.coroutines.CancellationException

fun Exception.rethrowIfCancellation() {
    if(this is CancellationException) throw this
}