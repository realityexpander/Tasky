package com.realityexpander.tasky.core.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.getBitmapFromVectorDrawable(@DrawableRes drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(this, drawableId)
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}