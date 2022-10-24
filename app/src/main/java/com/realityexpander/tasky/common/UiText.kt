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
    class StrRes(
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
    class StrOrStrRes(
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
    class StrResOrStr(
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
            is StrOrStrRes ->
                value?.let { "UiText.StrOrStrRes: value=$value" }
                    ?: resId?.let { "UiText.StrOrStrRes: resId=${this.resId}" }
                    ?: "UiText.StrOrStrRes=both null"
            is StrRes ->
                    "UiText.StrRes: resId=$resId"
            is StrResOrStr ->
                resId?.let { "UiText.StrResOrStr: resId=$resId" }
                    ?: value?.let { "UiText.StrResOrStr: value=$value" }
                    ?:  "UiText.StrResOrStr=both null"
        }
    }

    // Returns string "" if UiText is `None` or value is null.
    @Composable
    fun get(): String {
        return when(this) {
            is None -> ""
            is Str -> value ?: ""
            is StrRes -> stringResource(resId, *args)
            is StrOrStrRes -> value ?: stringResource(resId!!, *args)
            is StrResOrStr -> stringResource(resId!!, *args) ?: value ?: ""
        }
    }

    // Returns null if UiText is `None` or `value` is null.
    @Composable
    fun getNullable(): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is StrRes -> stringResource(resId, *args)
            is StrOrStrRes -> value ?: stringResource(resId!!, *args)
            is StrResOrStr -> stringResource(resId!!, *args)
        }
    }

    // For use in @Composable functions.
    // Returns the string "null" if UiText is `None` or `Str` with null value. (good for debugging)
    @Composable
    fun asString(): String {
        return when(this) {
            is None -> "null"
            is Str -> value ?: "null"
            is StrRes -> stringResource(resId, *args)
            is StrOrStrRes -> value ?: stringResource(resId!!, *args)
            is StrResOrStr -> resId?.let { stringResource(it, *args) } ?: value ?: "null"
        }
    }

    // For use in Contexts/Activities/Fragments.
    fun asString(context: Context): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is StrRes -> context.getString(resId, *args)
            is StrOrStrRes -> value ?: context.getString(resId!!, *args)
            is StrResOrStr -> resId?.let { context.getString(it, *args) } ?: value
        }
    }

    // Returns only the string value or null.
    fun asStrValueOrNull(): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is StrRes -> null
            is StrOrStrRes -> value
            is StrResOrStr -> null
        }
    }

    // Returns only the string resource id or null.
    fun asStrResIdOrNull(): Int? {
        return when(this) {
            is None -> null
            is Str -> null
            is StrRes -> resId
            is StrOrStrRes -> resId
            is StrResOrStr -> null
        }
    }
}
