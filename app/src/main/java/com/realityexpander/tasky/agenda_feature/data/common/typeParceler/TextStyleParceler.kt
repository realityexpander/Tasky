package com.realityexpander.tasky.agenda_feature.data.common.typeParceler

import android.os.Parcel
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.parcelize.Parceler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import logcat.logcat

// Note: Only saves the `fontSize` and `fontWeight` of the TextStyle
object TextStyleParceler : Parceler<TextStyle> {

    @Serializable
    class TextStyleForParceler(
        val fontSize: Float,
        val fontWeight: Int?,
    )

    override fun create(parcel: Parcel): TextStyle {
        val json = parcel.readString() ?: ""
        logcat { "TextStyleParceler.create() - json: $json" }

        val textStyleParceler = Json.decodeFromString(TextStyleForParceler.serializer(), json)
        return TextStyle(
            fontSize = textStyleParceler.fontSize.sp,
            fontWeight = textStyleParceler.fontWeight?.let { FontWeight(it) },
        )
    }

    override fun TextStyle.write(parcel: Parcel, flags: Int) {

        val textStyleForParceler = TextStyleForParceler(
            fontSize = fontSize.value,
            fontWeight = fontWeight?.weight,
        )

        val jsonString = Json.encodeToString(TextStyleForParceler.serializer(), textStyleForParceler)

        logcat { "TextStyleParceler.write() - jsonString: $jsonString" }
        parcel.writeString(jsonString)

    }
}