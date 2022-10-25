package com.realityexpander.tasky.common

import android.content.Context
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
open class UiText : Parcelable {

    // Example:
    // str = null
    // UiText.Str(str) -> asString() = null
    //
    // str = "Goodbye."
    // UiText.Str(str) -> asString() = "Goodbye."
    @Parcelize
    class Str(
        val value: String?,
    ): UiText()


    // Example:
    // resId = "Hello, %s!"
    // args = "World!"
    // UiText.StrRes(resId) -> asString() = "Hello, World!"
    //
    // resId = null -> compiler error (not allowed)
    @Parcelize
    class Res(
        @StringRes val resId: Int,
        vararg val args: @RawValue Any
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
    @Parcelize
    class StrOrRes(
        val value: String?,
        @StringRes val resId: Int? = null,
        vararg val args: @RawValue Any //= arrayOf()
    ): UiText()

    // Example:
    // resId = "Hello, %s!"
    // str = null
    // UiText.StrResOrStr(resId, str, "World") -> asString() = "Hello, World!"
    //
    // resId = null
    // UiText.StrResOrStr(resId, "Goodbye!") -> asString() = "Goodbye!"
    @Parcelize
    class ResOrStr(
        @StringRes val resId: Int? = null,
        val value: String? = null,
        vararg val args: @RawValue Any //= arrayOf()
    ): UiText()

    // Example:
    // asString() = null
    @Parcelize
    object None : UiText()

    // Useful for debugging
    override fun toString(): String {
        return when(this) {
            is None ->
                "UiText.None"
            is Str ->
                value?.let { "UiText.Str: value=$it" }
                    ?: "UiText.Str=null"
            is StrOrRes ->
                value?.let { "UiText.StrOrRes: value=$value" }
                    ?: resId?.let { "UiText.StrOrRes: resId=${this.resId}" }
                    ?: "UiText.StrOrRes=both null"
            is Res ->
                    "UiText.Res: resId=$resId"
            is ResOrStr ->
                resId?.let { "UiText.ResOrStr: resId=$resId" }
                    ?: value?.let { "UiText.ResOrStr: value=$value" }
                    ?:  "UiText.ResOrStr=both null"
            else -> {
                "UiText: Unknown type"
            }
        }
    }

    // If UiText is `None` or `value` is null this returns string "", or returns Resource ID.
    // Always returns a valid string. (good for use in display UI)
    @Composable
    fun get(): String {
        return when(this) {
            is None -> ""
            is Str -> value ?: ""
            is Res -> stringResource(resId, *args)
            is StrOrRes -> value ?: stringResource(resId!!, *args)
            is ResOrStr -> stringResource(resId!!, *args) ?: value ?: ""
            else -> {
                "UiText: Unknown type"
            }
        }
    }

    // If UiText is `None` or `value` is null this returns null, or returns Resource ID.
    // May return a null. (good for use in logical checks)
    @Composable
    fun getOrNull(): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is Res -> stringResource(resId, *args)
            is StrOrRes -> value ?: stringResource(resId!!, *args)
            is ResOrStr -> stringResource(resId!!, *args)
            else -> {
                "UiText: Unknown type"
            }
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
            else -> {
                "UiText: Unknown type"
            }
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
            else -> {
                "UiText: Unknown type"
            }
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
            else -> {
                "UiText: Unknown type"
            }
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
            else -> {
                throw Exception("UiText: Unknown type")
            }
        }
    }
}
