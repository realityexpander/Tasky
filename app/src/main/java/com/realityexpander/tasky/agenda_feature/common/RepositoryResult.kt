package com.realityexpander.tasky.agenda_feature.common

import com.realityexpander.tasky.core.presentation.common.util.UiText

sealed interface RepositoryResult<T> {
    data class Success<T>(
        val data: T? = null,
        val message: String? = null
    ) : RepositoryResult<T>

    data class Error<T>(
        val message: UiText,
        val originalMessage: String? = null
    ) : RepositoryResult<T>
}