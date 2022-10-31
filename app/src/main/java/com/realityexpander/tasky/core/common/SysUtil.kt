package com.realityexpander.tasky.core.common

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat

// Log messages
fun Any.log(msg: String) {
    Log.d(this::class.java.simpleName, msg)
}


// Repeat Strings
fun String.repeat(times: Int): String {
    return buildString {
        repeat(times) {
            append(this@repeat)
        }
    }
}

// String substring Range
fun String.substring(range: IntRange): String {
    return substring(range.first, range.last + 1)
}

// val mainStr = "Interesting"
// val substr = mainStr[2..8] // "teresti"
operator fun String.get(range: IntRange) =
    substring(range.first, range.last + 1)


// Android resources
fun Context.drawable(@DrawableRes resId: Int) =
    ResourcesCompat.getDrawable(resources, resId, null)

fun Context.font(@FontRes resId: Int) =
    ResourcesCompat.getFont(this, resId)

fun Context.dimen(@DimenRes resId: Int) =
    resources.getDimension(resId)

fun Context.anim(@AnimRes resId: Int) =
    AnimationUtils.loadAnimation(this, resId)


// Complex units for dimensions
fun Context.dpToPx(dp: Float) =
    dp * resources.displayMetrics.density

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

val Float.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )
/*
 * use as 18.dp
 * or 22.5f.sp
 */
val Int.dp get() = toFloat().dp
val Int.sp get() = toFloat().sp