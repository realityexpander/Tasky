package com.realityexpander.tasky.agenda_feature.data.common.serializers

import android.net.Uri
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import androidx.core.net.toUri


//@OptIn(ExperimentalSerializationApi::class)
//@Serializer(forClass = Uri::class)
object UriSerializer : KSerializer<Uri> {
    override val descriptor = PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Uri {
        return decoder.decodeString().toUri()
    }

    override fun serialize(encoder: Encoder, value: Uri) {
         encoder.encodeString(value.toString())
        // URI.create(value.toString()).toASCIIString()  // also works
        // encoder.encodeString(URLDecoder.decode(value.toString(), "UTF-8")) // also works
    }
}

// Local Test
fun main() {
    val uri = "https://www.google.com".toUri()
    val uriStr = uri.toString()
    val uri2 = uriStr.toUri()

    println("uri: $uri")
    println("uriStr: $uriStr")
    println("uri2: $uri2")

    println()
    println("uri == uri2: ${uri2 == uri}")
}
