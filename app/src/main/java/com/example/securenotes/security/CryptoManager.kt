package com.example.securenotes.security


import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi

import java.lang.IndexOutOfBoundsException
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.InvalidParameterException
import java.security.KeyStore
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CryptoManager {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    private val algorithm = "AES"
    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
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

    fun encryptStr(data: String): String {
        var key = "1234567890ABCDEFGH".toByteArray(Charsets.UTF_8)
        var ciphertxt = ByteArray(10)
        try {
            val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = key.copyOf(16)
            val secretKeySpec = SecretKeySpec(key, algorithm)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            val ivParameterSpec =
                IvParameterSpec(ByteArray(16)) // Use the same IV as used in encryption
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
            val input = data.toByteArray(Charsets.UTF_8)
            ciphertxt = cipher.doFinal(input)
        } catch (e: Exception) {
            Log.e("ERROR", "$e.message")
        }
        return Base64.encodeToString(ciphertxt, Base64.NO_WRAP)
    }

    fun decryptStr(data: String): String {
         var key = ""
        var decryptedText = ""
        try {
            /*val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = key.copyOf(16)*/
            val secretKeySpec = SecretKeySpec(key.toByteArray(), algorithm)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            val iv = cipher.iv
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(iv))

            // Check if data is null or empty
                val decodedData = Base64.decode(data, Base64.DEFAULT)
            if (decodedData.contentEquals(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0))) {
                Log.e("Error", "Decoding failed: invalid Base64 encoding")
            } else {
                // Process the decoded data
                val decryptedData = cipher.doFinal(decodedData)

                // Process the decrypted data
                decryptedText = String(decryptedData, Charsets.UTF_8)
                Log.d("Decrypted Text", decryptedText)
            }

                // Perform the decryption


        } catch (e: Exception) {
            Log.e("Error", "Error during decryption: ${e.message}")
        }
        return decryptedText
    }
    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    }

}

