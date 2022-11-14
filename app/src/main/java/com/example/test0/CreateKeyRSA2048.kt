package com.example.test0;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log
import java.security.*
import java.security.spec.MGF1ParameterSpec
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Base64.getEncoder
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

class CreateKeyRSA2048(private val scope0: String) {
    private val KEY_LENGTH_BIT = 2048
    private val KEY_PROVIDER_NAME = "AndroidKeyStore"
    private val KEYSTORE_INSTANCE_TYPE ="AndroidKeyStore"

    private lateinit var keyEntry: KeyStore.Entry

    @Suppress("ObjectPropertyName")
    private var _isSupported = false

    private val isSupported: Boolean
    get() = _isSupported


    internal fun init() :Boolean{

        val alias = scope0+".keypair"
        val keyStore = KeyStore.getInstance(KEYSTORE_INSTANCE_TYPE).apply{
            load(null)
        }

        var result: Boolean = initKeyStore(alias)

        keyEntry = keyStore.getEntry(alias,null)
        _isSupported = result


        return _isSupported
    }

    private fun initKeyStore(alias: String): Boolean{
        try {
            val kpg =
                KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_PROVIDER_NAME)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val parameterSpec = KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setAlgorithmParameterSpec(
                        RSAKeyGenParameterSpec(
                            KEY_LENGTH_BIT,
                            RSAKeyGenParameterSpec.F4
                        )
                    )
                    .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .setDigests(
                        KeyProperties.DIGEST_SHA1
                    )
                    .setUserAuthenticationRequired(false)
                    .build()
                kpg.initialize(parameterSpec)


                kpg.generateKeyPair()


            } else {
                return false
            }

            return true
        } catch (e: GeneralSecurityException) {
            return false
        }
    }

    fun getPubKey(): String {
        if(this::keyEntry.isInitialized) {
            val ke = this.keyEntry as KeyStore.PrivateKeyEntry

            return ke.certificate.publicKey.toString()
        }
        else{
            return "false"
        }
    }

    fun getPrvKey(): PrivateKey {
        if(!this::keyEntry.isInitialized) {
            Log.d("msg","[ERROR] Failed to get private key.")
        }

        val ke = this.keyEntry as KeyStore.PrivateKeyEntry
        return ke.privateKey
    }

    fun encrypt(plainText: String, pubkey: PublicKey): ByteArray {
        val algorithmParameterSpec = OAEPParameterSpec("SHA-1",
            "MGF1",
            MGF1ParameterSpec.SHA1,
            PSource.PSpecified.DEFAULT)

        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").apply {
            init(Cipher.ENCRYPT_MODE, pubkey, algorithmParameterSpec)
        }
        val bytes = plainText.toByteArray(Charsets.UTF_8)
        println("length before encryption:${bytes.size}")

        val encryptedBytes = cipher.doFinal(bytes)
        println("length after decryption:${encryptedBytes.size}")
        return encryptedBytes
    }

    fun decrypt(encryptedBytes: ByteArray, prvkey: PrivateKey): String {
        val algorithmParameterSpec = OAEPParameterSpec("SHA-1",
            "MGF1",
            MGF1ParameterSpec.SHA1,
            PSource.PSpecified.DEFAULT)
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").apply {
            init(Cipher.DECRYPT_MODE, prvkey, algorithmParameterSpec)
        }
        println("encryptedBytes: $encryptedBytes")
        println("size of encryptedBytes: ${encryptedBytes.size}")
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        val decryptedString = decryptedBytes.toString(Charsets.UTF_8)
        println("decrypted bytes:${decryptedBytes}")
        println("decrypted bytes to string: ${decryptedString}")
        return decryptedString
    }
}