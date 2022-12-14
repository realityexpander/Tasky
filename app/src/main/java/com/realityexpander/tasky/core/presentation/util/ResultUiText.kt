package com.realityexpander.tasky.core.presentation.util

sealed class ResultUiText<T> {
    data class Success<T>(
        val data: T? = null,
        val message: UiText? = null,
        val apiMessage: String? = null
    ) : ResultUiText<T>()

    data class Error<T>(
        val message: UiText,
        val exceptionMessage: String? = null
    ) : ResultUiText<T>()
}