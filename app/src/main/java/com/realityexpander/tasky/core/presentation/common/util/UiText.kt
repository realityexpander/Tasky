package com.realityexpander.tasky.core.presentation.common.util

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

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
        val value: String?,
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
        val value: String?,
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
        val value: String? = null,
        vararg val args: @RawValue Any //= arrayOf()
    ): UiText()

    // Example:
    // .get = null
    @Parcelize
    object None : UiText()

    /////////////////// IDENTIFIERS ///////////////////

    val isNone: Boolean
        get() = this is None

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
            is None -> ""
            is Str -> value ?: ""
            is Res -> stringResource(resId, *args)
            is StrOrRes -> value ?: resId?.let{ stringResource(resId, *args) } ?: ""
            is ResOrStr -> resId?.let{ stringResource(resId, *args) } ?: value ?: ""
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns the string of the UiText, or null if it is not a valid `value` string and not a valid string resource.
    // (good for use in logical checks)
    val getOrNull: String?
        @Composable
        get() = when(this) {
            is None -> null
            is Str -> value
            is Res -> stringResource(resId, *args)
            is StrOrRes -> value ?: resId?.let{ stringResource(resId, *args) }
            is ResOrStr -> resId?.let{ stringResource(resId, *args) } ?: value
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns only the `value` string value or null if its missing.
    // (good for logical checks if you just want the string and not the resource)
    val str: String?
        @Composable
        get() = when(this) {
            is None -> null
            is Str -> value
            is Res -> null
            is StrOrRes -> value
            is ResOrStr -> null
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns only the resource string or null if its missing.
    val res: String?
        @Composable
        get() = when(this) {
            is None -> null
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
            is None -> null
            is Str -> value
            is Res -> stringResource(resId, *args)
            is StrOrRes -> value ?: resId?.let{ stringResource(resId, *args) }
            is ResOrStr -> resId?.let{ stringResource(resId, *args) } ?: value
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns the resource string, or the `value` string, or null if both are missing.
    val resOrStr: String?
        @Composable
        get() = when(this) {
            is None -> null
            is Str -> value
            is Res -> stringResource(resId, *args)
            is StrOrRes -> value ?: resId?.let{ stringResource(resId, *args) }
            is ResOrStr -> resId?.let{ stringResource(resId, *args) } ?: value
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }

    // Returns only the string Resource ID or null if its missing.
    // (good for logical checks or if you just want the resource Id and not the string)
    val asResIdOrNull: Int?
        get() = when(this) {
            is None -> null
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
            is None -> "null"
            is Str -> value ?: "null"
            is Res -> stringResource(resId, *args)
            is StrOrRes -> value ?: resId?.let{ stringResource(resId, *args) } ?: "null"
            is ResOrStr -> resId?.let { stringResource(it, *args) } ?: value ?: "null"
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }
    }

    // For use in Contexts/Activities/Fragments.
    fun asString(context: Context): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is Res -> context.getString(resId, *args)
            is StrOrRes -> value ?: resId?.let { context.getStringSafe(it, *args) }
            is ResOrStr -> resId?.let { context.getStringSafe(it, *args) } ?: value
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
    // Returns null if UiText is `None` or `Str` is null value.
    // (good for testing)
    fun asStrOrNull(): String? {
        return when(this) {
            is None -> null
            is Str -> value
            is Res -> null
            is StrOrRes -> value
            is ResOrStr -> value
            else -> {
                throw Exception("Invalid UiText type: ${this::class.java.simpleName}")
            }
        }
    }

    // Useful for debugging (displayed in the debugger)
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
                "UiText: Unknown type: ${this::class.java.simpleName}"
            }
        }
    }

}
