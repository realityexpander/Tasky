package com.realityexpander.tasky.agenda_feature.common

sealed interface RepositoryResult {
    object Success : RepositoryResult
    data class Error(val message: String) : RepositoryResult
}