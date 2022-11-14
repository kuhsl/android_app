package com.example.test0;

import android.os.Build;
        import android.security.keystore.KeyGenParameterSpec;
        import android.security.keystore.KeyProperties;
import android.util.Base64
import android.util.Log
import java.security.*
import java.security.spec.MGF1ParameterSpec
import java.security.spec.RSAKeyGenParameterSpec;
        import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import kotlin.math.min

class RSA2048Test() {
    private val KEY_LENGTH_BIT = 2048
    private val KEY_PROVIDER_NAME = "AndroidKeyStore"
    private val KEYSTORE_INSTANCE_TYPE ="AndroidKeyStore"


    private lateinit var keyEntry: KeyStore.Entry

    @Suppress("ObjectPropertyName")
    private var _isSupported = false

    private val isSupported: Boolean
    get() = _isSupported


    internal fun init() :Boolean{

        val alias = "scope"
        val keyStore = KeyStore.getInstance(KEYSTORE_INSTANCE_TYPE).apply{
            load(null)
        }

        var result: Boolean = initKeyStore(alias)

        val keyEntry = keyStore.getEntry(alias,null)
        _isSupported = result

        //TODO: RSA 2048 test
        val ke= keyEntry as KeyStore.PrivateKeyEntry
        val encryptedCiphertext = encrypt("안녕하세요", ke.certificate.publicKey)

        Log.d("encrypted:",encryptedCiphertext.toString())
        Log.d("encrypted_tostring:", Base64.encodeToString(encryptedCiphertext, Base64.DEFAULT))

        val encryptedCiphertext_base64=Base64.encodeToString(encryptedCiphertext, Base64.DEFAULT)

        val decryptedPlaintext=decrypt(encryptedCiphertext_base64, ke.privateKey)


        return _isSupported
    }

    private fun initKeyStore(alias: String): Boolean{
        try {
            val kpg=KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_PROVIDER_NAME)
            println("android version:${Build.VERSION.SDK_INT}")
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

                //TODO: RSA 2048 test
                /*
                val keys=kpg.generateKeyPair()

                val publicKey = keys.public
                val privateKey = keys.private

                val encryptedCiphertext=encrypt("안녕하세요",publicKey)
                Log.d("encrypted:",encryptedCiphertext.toString())
                Log.d("encrypted_tostring:", Base64.encodeToString(encryptedCiphertext, Base64.DEFAULT))

                val encryptedCiphertext_base64=Base64.encodeToString(encryptedCiphertext, Base64.DEFAULT)

                val decryptedPlaintext=decrypt(encryptedCiphertext_base64, privateKey)

                Log.d("decrypted:",decryptedPlaintext)
                */
            } else {
                return false
            }

            return true
        } catch (e: GeneralSecurityException) {
            return false
        }
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

    fun decrypt(encryptedCipherText: String, prvkey: PrivateKey): String {
        val algorithmParameterSpec = OAEPParameterSpec("SHA-1",
            "MGF1",
            MGF1ParameterSpec.SHA1,
            PSource.PSpecified.DEFAULT)
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").apply {
            init(Cipher.DECRYPT_MODE, prvkey, algorithmParameterSpec)
        }
        //val encryptedBytes = Base64.decode("",Base64.DEFAULT)
        val encryptedBytes = Base64.decode(encryptedCipherText,Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        val decryptedString = decryptedBytes.toString(Charsets.UTF_8)
        println("decrypted bytes:${decryptedBytes}")
        println("decrypted bytes to string: ${decryptedString}")

        test(encryptedCipherText)
        return decryptedBytes.toString(Charsets.UTF_8)
    }

    fun test(encryptedCipherText: String){
        val alias = "scope"
        val keyStore = KeyStore.getInstance(KEYSTORE_INSTANCE_TYPE).apply {
            load(null)
        }

        if (keyStore.containsAlias(alias)) {
            val keyEntry = keyStore.getEntry(alias, null)
            val ke = keyEntry as KeyStore.PrivateKeyEntry
            val prvkey = ke.privateKey



            val algorithmParameterSpec = OAEPParameterSpec(
                "SHA-1",
                "MGF1",
                MGF1ParameterSpec.SHA1,
                PSource.PSpecified.DEFAULT
            )
            val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").apply {
                init(Cipher.DECRYPT_MODE, prvkey, algorithmParameterSpec)
            }
            val encryptedBytes = Base64.decode(encryptedCipherText,Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            val decryptedString = decryptedBytes.toString(Charsets.UTF_8)
            Log.d("test"," decrypted bytes:${decryptedBytes}")
            Log.d("test"," decrypted bytes to string: ${decryptedString}")
        }

    }
}