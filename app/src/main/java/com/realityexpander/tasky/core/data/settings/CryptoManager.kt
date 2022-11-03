package com.realityexpander.tasky.core.data.settings

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

// Source video
// https://www.youtube.com/watch?v=D_q07P1sfcc

// Stack overflow issues
// https://stackoverflow.com/questions/17234359/javax-crypto-illegalblocksizeexception-input-length-must-be-multiple-of-16-whe
// https://stackoverflow.com/questions/57590740/problem-with-decryption-cipher-in-android-aes-ctr-nopadding
// https://stackoverflow.com/questions/14371709/android-crittografy-cipher-decrypt-doesnt-work
// https://security.stackexchange.com/questions/29993/aes-cbc-padding-when-the-message-length-is-a-multiple-of-the-block-size
// https://stackoverflow.com/questions/33557244/aes-decryption-not-working-for-more-than-16-bytes
// https://stackoverflow.com/questions/26950251/android-cipher-doesnt-decrypt-first-16-bytes-characters-of-encrypted-data
// https://developer.android.com/topic/security/data
//

@RequiresApi(Build.VERSION_CODES.M)
class CryptoManager {


    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher
        get() = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey())
        }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey =
            keyStore.getEntry(KEYSTORE_ENTRY_NAME, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                        KEYSTORE_ENTRY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
        // Include the Initialization Vector (IV) at the start of the data to encrypt
        val encryptedBytes = encryptCipher.doFinal(encryptCipher.iv + bytes)

        outputStream.use {
            it.write(encryptCipher.iv.size)
            it.write(encryptCipher.iv)

            // Save the size as Int - WHY DOES IT NOT JUST WRITE AN INT AS 2 BYTES? IN 2022? WTFFFFFFF??? IT ACCEPTS AN INT!!!!
            it.write((encryptedBytes.size and 0xFF00) shr (8)) // mask to get high order byte of Int, write as byte
            it.write(encryptedBytes.size and 0x00FF) // mask to get low order byte of Int, write as byte

            it.write(encryptedBytes)
        }
        return encryptedBytes
    }

    fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            // Get the size of the encrypted data as an Int
            val encryptedBytesSizeHighOrderByte = it.read()
            val encryptedBytesSizeLowOrderByte = it.read()
            val encryptedBytesSize =
                (encryptedBytesSizeHighOrderByte shl (8)) or // rebuild the Int (why are we still doing this in 2022???)
                (encryptedBytesSizeLowOrderByte)

            val encryptedBytes = ByteArray(encryptedBytesSize)
            it.read(encryptedBytes)

            val decipheredByteArray = getDecryptCipherForIv(iv)
                .doFinal(encryptedBytes)

            // Strip the Initialization Vector (usually first 16 bytes) to get the actual unencrypted data
            decipheredByteArray
                .toList()
                .subList(ivSize, decipheredByteArray.size) // skip the IV
                .toByteArray()
            //.decodeToString()  // for debugging
            //.toByteArray()     // for debugging
        }
    }

    companion object {
        private const val KEYSTORE_ENTRY_NAME = "secretTasky"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7 // uses variable block size
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

}