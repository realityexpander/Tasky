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

    @Composable
    fun asString(): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is StrRes -> stringResource(resId, *args)
            is StrOrStrRes -> value ?: stringResource(resId!!, *args)
            is StrResOrStr -> resId?.let { stringResource(it, *args) } ?: value
        }
    }

    fun asString(context: Context): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is StrRes -> context.getString(resId, *args)
            is StrOrStrRes -> value ?: context.getString(resId!!, *args)
            is StrResOrStr -> resId?.let { context.getString(it, *args) } ?: value
        }
    }
}
