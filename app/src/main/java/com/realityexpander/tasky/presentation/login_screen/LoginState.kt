package com.realityexpander.tasky.presentation.login_screen

data class State(
    var isLoading: Boolean = false,
    var isLoggedIn: Boolean = false,
    var isError: Boolean = false,
    var errorMessage: String = "",
    var statusMessage: String = ""
)