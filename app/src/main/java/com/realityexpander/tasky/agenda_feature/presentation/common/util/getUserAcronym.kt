package com.realityexpander.tasky.agenda_feature.presentation.common.util

fun getUserAcronym(username: String): String {
    if(username.isBlank()) return "??"
    if(username.length<2) return username.uppercase()

    println("username: $username")

    val words = username.split(" ")
    println("words: $words, size: ${words.size}")
    if (words.size > 1) {
        return (words[0].substring(0, 1) + words[1].substring(0, 1)).uppercase()
    }

    return username.substring(0, 2).uppercase()
}