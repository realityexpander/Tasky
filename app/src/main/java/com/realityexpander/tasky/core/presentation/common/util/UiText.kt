package com.realityexpander.tasky.core.presentation.common.util

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

// String resource wrapper
// Allows storing normal strings and resource-based strings in the same data class.
// Depending on the context, the string will be resolved to a string-resource or a normal string.

@Parcelize
open class UiText : Parcelable {

    // Example:
    // str = null
    // UiText.Str(str) -> .get = ""
    //
    // str = "Goodbye."
    // UiText.Str(str) -> .get = "Goodbye."
    @Parcelize
    class Str(
        val string: String?,
    ): UiText()


    // Example:
    // resId = "Hello, %s!"
    // args = "World!"
    // UiText.Res(resId, args) -> .get = "Hello, World!"
    //
    // resId = "Goodbye."
    // UiText.Res(resId) -> .get = "Goodbye."
    //
    // resId = null
    // UiText.Res(resId) -> compiler error (not allowed)
    @Parcelize
    class Res(
        @StringRes val resId: Int,
        vararg val args: @RawValue Any
    ): UiText()

    // Example:
    // str = "Goodbye."
    // resId = "Hello, %s!"
    // args = "World!"
    // UiText.StrOrRes(resId, str, args) -> .get = "Goodbye."
    //
    // str = null
    // resId = "Hello, %s!"
    // args = "World!"
    // UiText.StrOrRes(resId, str, args) -> .get = "Hello, World!"
    @Parcelize
    class StrOrRes(
        val string: String?,
        @StringRes val resId: Int? = null,
        vararg val args: @RawValue Any
    ): UiText()

    // Example:
    // resId = "Hello, %s!"
    // args = "World!"
    // str = "Goodbye."
    // UiText.ResOrStr(resId, str, args) -> .get = "Hello, World!"
    //
    // resId = null
    // args = "World!"
    // str = "Goodbye."
    // UiText.ResOrStr(resId, str, args) -> .get = "Goodbye!"
    @Parcelize
    class ResOrStr(
        @StringRes val resId: Int? = null,
        val string: String? = null,
        vararg val args: @RawValue Any //= arrayOf()
    ): UiText()


    /////////////////// IDENTIFIERS ///////////////////


    val isStr: Boolean
        get() = this is Str

    val isRes: Boolean
        get() = this is Res

    val isStrOrRes: Boolean
        get() = this is StrOrRes

    val isResOrStr: Boolean
        get() = this is ResOrStr

    /////////////////// GETTERS ////////////////////////

    // Returns the string of the UiText, or an empty string if it is not a `value` valid string or a string resource.
    // (good for use in UI display)
    val get: String
        @Composable
        get() = when(this) {
            is Str -> string ?: ""
            is Res -> stringResource(resId, *args)
            is StrOrRes -> string ?: resId?.let{ stringResource(resId, *args) } ?: ""
            is ResOrStr -> resId?.let{ stringResource(resId, *args) } ?: string ?: ""
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns the string of the UiText, or null if it is not a valid `value` string and not a valid string resource.
    // (good for use in logical checks)
    val getOrNull: String?
        @Composable
        get() = when(this) {
            is Str -> string
            is Res -> stringResource(resId, *args)
            is StrOrRes -> string ?: resId?.let{ stringResource(resId, *args) }
            is ResOrStr -> resId?.let{ stringResource(resId, *args) } ?: string
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns only the `value` string value or null if its missing.
    // (good for logical checks if you just want the string and not the resource)
    val str: String?
        @Composable
        get() = when(this) {
            is Str -> string
            is Res -> null
            is StrOrRes -> string
            is ResOrStr -> null
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns only the resource string or null if its missing.
    val res: String?
        @Composable
        get() = when(this) {
            is Str -> null
            is Res -> stringResource(resId, *args)
            is StrOrRes -> resId?.let{ stringResource(resId, *args) }
            is ResOrStr -> resId?.let{ stringResource(resId, *args) }
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns the `value` string, or the resource string, or null if both are missing.
    val strOrRes: String?
        @Composable
        get() = when(this) {
            is Str -> string
            is Res -> stringResource(resId, *args)
            is StrOrRes -> string ?: resId?.let{ stringResource(resId, *args) }
            is ResOrStr -> resId?.let{ stringResource(resId, *args) } ?: string
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns the resource string, or the `value` string, or null if both are missing.
    val resOrStr: String?
        @Composable
        get() = when(this) {
            is Str -> string
            is Res -> stringResource(resId, *args)
            is StrOrRes -> string ?: resId?.let{ stringResource(resId, *args) }
            is ResOrStr -> resId?.let{ stringResource(resId, *args) } ?: string
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns only the string Resource ID or null if its missing.
    // (good for logical checks or if you just want the resource Id and not the string)
    val asResIdOrNull: Int?
        get() = when(this) {
            is Str -> null
            is Res -> resId
            is StrOrRes -> resId
            is ResOrStr -> resId
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    /////////////////////// For Context/Fragment/Activity/View //////////////////////////

    // For use in @Composable functions.
    // Returns the string "null" if UiText is `None` or `Str` is null value.
    // (good for debugging)
    @Composable
    fun asString(): String {
        return when(this) {
            is Str -> string ?: "null"
            is Res -> stringResource(resId, *args)
            is StrOrRes -> string ?: resId?.let{ stringResource(resId, *args) } ?: "null"
            is ResOrStr -> resId?.let { stringResource(it, *args) } ?: string ?: "null"
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }
    }

    // For use in Contexts/Activities/Fragments.
    fun asString(context: Context): String? {
        return when(this) {
            is Str -> string
            is Res -> context.getString(resId, *args)
            is StrOrRes -> string ?: resId?.let { context.getStringSafe(it, *args) }
            is ResOrStr -> resId?.let { context.getStringSafe(it, *args) } ?: string
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }
    }

    /////////////////////// HELPERS //////////////////////////


    // Safely returns a resource string that may not have been passed correct parameters at construction time.
    // Only works in Context/Fragment/Activity/View scope.
    private fun Context.getStringSafe(@StringRes resId: Int, vararg args: Any): String {
        return try {
            getString(resId, *args)
        } catch (e: Exception) {
            e.printStackTrace()
            getString(resId)
        }
    }

    // Useful for plain kotlin classes that don't have access to @Composable functions.
    // Returns null if Str is `None` or `Str` is null value. Ignores the Resource value
    // (good for testing)
    fun asStrOrNull(): String? {
        return when(this) {
            is Str -> string
            is Res -> null
            is StrOrRes -> string
            is ResOrStr -> string
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }
    }

    // Useful for debugging (displayed in the debugger)
    override fun toString(): String {
        return when(this) {
            is Str ->
                string?.let { "UiText.Str: string=$it" }
                    ?: "UiText.Str=null"
            is StrOrRes ->
                string?.let { "UiText.StrOrRes: string=$string" }
                    ?: resId?.let { "UiText.StrOrRes: resId=${this.resId}" }
                    ?: "UiText.StrOrRes=both null"
            is Res ->
                    "UiText.Res: resId=$resId"
            is ResOrStr ->
                resId?.let { "UiText.ResOrStr: resId=$resId" }
                    ?: string?.let { "UiText.ResOrStr: string=$string" }
                    ?:  "UiText.ResOrStr=both null"
            else -> {
                "UiText: Unknown type: ${this::class.java.simpleName}"
            }
        }
    }

}

// Local test
fun main() {
    val uiText = UiText.Str("Hello World")
    println(uiText)

    // need to use in @Composable function
//    println(uiText.str)
//    println(uiText.res)
//    println(uiText.strOrRes)
//    println(uiText.resOrStr)

    println(uiText.asResIdOrNull)
    println(uiText.asStrOrNull())
}
