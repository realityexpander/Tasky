@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.realityexpander.tasky.core.data.settings

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class AppSettingsSerializer(
    private val encrypted: Boolean = true
) : Serializer<AppSettings> {

    override val defaultValue: AppSettings
        get() = AppSettings()

    private val cryptoManager: CryptoManager by lazy {
        CryptoManager()
    }

    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            val inputBytes = if(encrypted) {
                cryptoManager.decrypt(input)
            } else {
                input.readBytes()
            }

            Json.decodeFromString(
                deserializer = AppSettings.serializer(),
                string = inputBytes.decodeToString()
            ).also {
                if(!encrypted) Log.d("APP_SETTINGS_READ", it.toString())
            }
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        } catch(e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        withContext(Dispatchers.IO) {
            try {
                val outputBytes =
                    Json.encodeToString(
                        serializer = AppSettings.serializer(),
                        value = t
                    ).encodeToByteArray()

                if (encrypted) {
                    cryptoManager.encrypt(
                        bytes = outputBytes,
                        outputStream = output
                    )
                } else {
                    output.write(outputBytes)
                    Log.d("APP_SETTINGS_WRITE", t.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
