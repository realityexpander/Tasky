package com.realityexpander.tasky.core.util

import java.util.*

fun getUniqueInteger(): Int {
    return Date().time.toInt()
    // return System.currentTimeMillis().toInt() // alternate implementation
}

fun UuidStr.toIntegerHashCodeOfUUIDString(): Int {
    return UUID.fromString(this).hashCode()
}
