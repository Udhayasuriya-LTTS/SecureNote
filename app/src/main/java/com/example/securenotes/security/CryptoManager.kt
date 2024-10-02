package com.example.securenotes.security


import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.securenotes.model.Note
import kotlinx.serialization.json.Json
import java.lang.IndexOutOfBoundsException
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.InvalidParameterException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(algorithm)
        val secretKey = getKey()
        Log.d("KEY", "CHECK $secretKey $data")
        var encryptedBytes: ByteArray? = ByteArray(10)
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            encryptedBytes = cipher.doFinal(data.toByteArray())

        } catch (e: Exception) {
            when (e) {
                is InvalidKeyException ->
                    println("invalid secret key")

                is InvalidParameterException ->
                    println("invalid parameter")

                is NoSuchPaddingException ->
                    println("Padding Exception")

                is NoSuchAlgorithmException ->
                    println("Algorithm Exception")

                is IndexOutOfBoundsException ->
                    println("Attempt to get length of null array")

                is IllegalStateException ->
                    println("cipher not Initialized")

                is NullPointerException ->
                    println("Null input data")

                is BadPaddingException ->
                    println("incorrect Padding or corrupted ciphertext")

                is IllegalBlockSizeException ->
                    println("input data size exceeds block size")

                else ->
                    println("unKnown error:$e")

            }

        }
        Log.d("ENCRYPT",Base64.getEncoder().encodeToString(encryptedBytes)
        )

        return Base64.getEncoder().encodeToString(encryptedBytes)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(encryptedData:String):String{
        val cipher=Cipher.getInstance(algorithm)
        val secretKey=getKey()
        var decryptedBytes = ByteArray(10)
        if(encryptedData.isNotEmpty()) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, secretKey)
                decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData))
            } catch (e: Exception) {
                when (e) {
                    is InvalidKeyException ->
                        println("invalid secret key")

                    is InvalidParameterException ->
                        println("invalid parameter")

                    is NoSuchPaddingException ->
                        println("Padding Exception")

                    is NoSuchAlgorithmException ->
                        println("Algorithm Exception")

                    is IndexOutOfBoundsException ->
                        println("Attempt to get length of null array")

                    is IllegalStateException ->
                        println("cipher not Initialized")

                    is NullPointerException ->
                        println("Null input data decrypt")

                    is BadPaddingException ->
                        println("incorrect Padding or corrupted ciphertext")

                    is IllegalBlockSizeException ->
                        println("input data size exceeds block size")

                    else ->
                        println("unKnown error:$e")

                }

            }
        }else{
            Log.d("ERROR","NO DATA")
        }
        Log.d("ERROR","STRING $decryptedBytes")


        return  String(decryptedBytes)

    }
    /*fun decrypt(encryptedData: List<Note>): List<Note> {
        val decryptedData = mutableListOf<Note>()
        val cipher = Cipher.getInstance(algorithm)
        val secretKey = getKey()

        for (data in encryptedData) {

            val str=data.toString()
            Log.d("ERROR",str)

            try {
                cipher.init(Cipher.DECRYPT_MODE, secretKey)
                var decryptedBytes: ByteArray
                data.let {
                    decryptedBytes = Base64.decode(str, Base64.DEFAULT)
                }

                Log.d("ERROR", "decyp$decryptedBytes")
                val json = String(cipher.doFinal(decryptedBytes))
                val note = Json.decodeFromString<Note>(json)
                decryptedData.add(note)
            } catch (e: Exception) {
                when (e) {
                    is InvalidKeyException ->
                        println("invalid secret key")

                    is InvalidParameterException ->
                        println("invalid parameter")

                    is NoSuchPaddingException ->
                        println("Padding Exception")

                    is NoSuchAlgorithmException ->
                        println("Algorithm Exception")

                    is IndexOutOfBoundsException ->
                        println("Attempt to get length of null array")

                    is IllegalStateException ->
                        println("cipher not Initialized")

                    is NullPointerException ->
                        println("Null input data decrypted")

                    is BadPaddingException ->
                        println("incorrect Padding or corrupted ciphertext")

                    is IllegalBlockSizeException ->
                        println("input data size exceeds block size")

                    else ->
                        println("unKnown error:$e")

                }
            }
        }

        return decryptedData

    }*/

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    }

}

