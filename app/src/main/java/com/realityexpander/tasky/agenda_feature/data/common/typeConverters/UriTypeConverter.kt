package com.realityexpander.tasky.agenda_feature.data.common.typeConverters

import android.net.Uri
import androidx.room.TypeConverter

class UriTypeConverter {
    @TypeConverter
    fun fromUri(uri: Uri): String = uri.toString()

    @TypeConverter
    fun toUri(uriStr: String): Uri = Uri.parse(uriStr)
}

// local test
fun main() {
    val uriConverter = UriTypeConverter()

    val uri = Uri.parse("https://www.google.com")
    val uriStr = uriConverter.fromUri(uri)
    val uri2 = uriConverter.toUri(uriStr)

    println("uri: $uri")
    println("uriStr: $uriStr")
    println("uri2: $uri2")

    println()
    println("uri == uri2: ${uri2 == uri}")
}