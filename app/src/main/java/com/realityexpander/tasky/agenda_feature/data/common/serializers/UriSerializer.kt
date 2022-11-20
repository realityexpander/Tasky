package com.realityexpander.tasky.agenda_feature.data.common.serializers

import android.net.Uri
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Uri::class)
object UriSerializer : KSerializer<Uri> {
    override val descriptor = PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Uri {
        return Uri.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Uri) {
         encoder.encodeString(value.toString())
        // URI.create(value.toString()).toASCIIString()
//        encoder.encodeString(URLDecoder.decode(value.toString(), "UTF-8"))
    }
}

// Local Test
fun main() {
    val uri = Uri.parse("https://www.google.com")
    val uriStr = uri.toString()
    val uri2 = Uri.parse(uriStr)

    println("uri: $uri")
    println("uriStr: $uriStr")
    println("uri2: $uri2")

    println()
    println("uri == uri2: ${uri2 == uri}")
}