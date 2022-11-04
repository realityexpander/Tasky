package com.realityexpander.tasky.agenda_feature.presentation.common.util

fun getUserAcronym(username: String): String {
    if(username.isBlank()) return ".."
    if(username.length<2) return username.uppercase()

    val words = username.split(" ")
    if (words.size > 1) {
        return (words.first().substring(0, 1) + words.last().substring(0, 1)).uppercase()
    }

    return username.substring(0, 2).uppercase()
}

fun main() {
    // test getUserAcronym
    println(getUserAcronym("John Doe") == "JD")
    println(getUserAcronym("John") == "JO")
    println(getUserAcronym("J") == "J")
    println(getUserAcronym("") == "??")
    println(getUserAcronym("John Doe Smith") == "JS")
    println(getUserAcronym("John Doe Smith Jr.") == "JJ")
    println(getUserAcronym("John Doe Smith Jr. III") == "JI")
    println(getUserAcronym("John Doe Smith Jr. III IV") == "JI")
    println(getUserAcronym("John Doe Smith Jr. III IV V") == "JV")
}