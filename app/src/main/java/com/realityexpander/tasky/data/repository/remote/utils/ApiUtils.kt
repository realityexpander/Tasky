package com.realityexpander.tasky.common

import org.json.JSONObject

fun getErrorBodyMessage(errorBody: String?): String {
    if(errorBody.isNullOrBlank())
        return "Unknown Error"

    val err = JSONObject(errorBody)
    return try {
        err.getString("message")
    } catch(e: Exception) {
        "Unknown Error"
    }
}