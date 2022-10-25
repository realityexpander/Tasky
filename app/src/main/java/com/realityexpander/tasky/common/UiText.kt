package com.realityexpander.tasky.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes

sealed class UiText {

    // Example:
    // str = null
    // UiText.Str(str) -> asString() = null
    //
    // str = "Goodbye."
    // UiText.Str(str) -> asString() = "Goodbye."
    data class Str(
        val value: String?,
    ): UiText()


    // Example:
    // resId = "Hello, %s!"
    // args = "World!"
    // UiText.StrRes(resId) -> asString() = "Hello, World!"
    //
    // resId = null -> compiler error (not allowed)
    class Res(
        @StringRes val resId: Int,
        vararg val args: Any
    ): UiText()

    // Example:
    // str = "Goodbye."
    // resId = "Hello, %s!"
    // args = "World!"
    // UiText.StrOrStrRes(resId) -> asString() = "Goodbye."
    //
    // str = null
    // resId = "Hello, %s!"
    // args = "World!"
    // UiText.StrOrStrRes(resId) -> asString() = "Hello, World!"
    class StrOrRes(
        val value: String?,
        @StringRes val resId: Int? = null,
        vararg val args: Any = arrayOf()
    ): UiText()

    // Example:
    // resId = "Hello, %s!"
    // str = null
    // UiText.StrResOrStr(resId, str, "World") -> asString() = "Hello, World!"
    //
    // resId = null
    // UiText.StrResOrStr(resId, "Goodbye!") -> asString() = "Goodbye!"
    class ResOrStr(
        @StringRes val resId: Int? = null,
        val value: String? = null,
        vararg val args: Any = arrayOf()
    ): UiText()

    // Example:
    // asString() = null
    object None : UiText()

    override fun toString(): String {
        return when(this) {
            is None ->
                "UiText.None"
            is Str ->
                value?.let { "UiText.Str: value=$it" }
                    ?: "UiText.Str=null"
            is StrOrRes ->
                value?.let { "UiText.StrOrStrRes: value=$value" }
                    ?: resId?.let { "UiText.StrOrStrRes: resId=${this.resId}" }
                    ?: "UiText.StrOrStrRes=both null"
            is Res ->
                    "UiText.StrRes: resId=$resId"
            is ResOrStr ->
                resId?.let { "UiText.StrResOrStr: resId=$resId" }
                    ?: value?.let { "UiText.StrResOrStr: value=$value" }
                    ?:  "UiText.StrResOrStr=both null"
        }
    }

    // If UiText is `None` or `value` is null return string "", or return Resource ID.
    // Always returns a valid string. (good for use in display UI)
    @Composable
    fun get(): String {
        return when(this) {
            is None -> ""
            is Str -> value ?: ""
            is Res -> stringResource(resId, *args)
            is StrOrRes -> value ?: stringResource(resId!!, *args)
            is ResOrStr -> stringResource(resId!!, *args) ?: value ?: ""
        }
    }

    // If UiText is `None` or `value` is null return null, or return Resource ID.
    // May return a null. (good for use in logical checks)
    @Composable
    fun getNullable(): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is Res -> stringResource(resId, *args)
            is StrOrRes -> value ?: stringResource(resId!!, *args)
            is ResOrStr -> stringResource(resId!!, *args)
        }
    }

    // For use in @Composable functions.
    // Returns the string "null" if UiText is `None` or `Str` is null value. (good for debugging)
    @Composable
    fun asString(): String {
        return when(this) {
            is None -> "null"
            is Str -> value ?: "null"
            is Res -> stringResource(resId, *args)
            is StrOrRes -> value ?: stringResource(resId!!, *args)
            is ResOrStr -> resId?.let { stringResource(it, *args) } ?: value ?: "null"
        }
    }

    // For use in Contexts/Activities/Fragments.
    fun asString(context: Context): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is Res -> context.getString(resId, *args)
            is StrOrRes -> value ?: context.getString(resId!!, *args)
            is ResOrStr -> resId?.let { context.getString(it, *args) } ?: value
        }
    }

    // Returns only the string value or null if its missing.
    // (good for logical checks if you just want the string and not the resource)
    fun asStrOrNull(): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is Res -> null
            is StrOrRes -> value
            is ResOrStr -> null
        }
    }

    // Returns only the string Resource ID or null if its missing.
    // (good for logical checks if you just want the resource and not the string)
    fun asResOrNull(): Int? {
        return when(this) {
            is None -> null
            is Str -> null
            is Res -> resId
            is StrOrRes -> resId
            is ResOrStr -> null
        }
    }
}
