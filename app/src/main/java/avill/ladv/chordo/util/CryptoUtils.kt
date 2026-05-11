package avill.ladv.chordo.util

import android.util.Base64
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Utility extensions for AES Encryption and Decryption.
 */

/**
 * Encrypts the string using AES/CBC/PKCS5Padding.
 * @param secretKey A 32-character string used as the key.
 * @return Base64 encoded encrypted string or null if encryption fails.
 */
fun String.encrypt(secretKey: String): String? {
    return try {
        val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(this.toByteArray(StandardCharsets.UTF_8))
        Base64.encodeToString(encrypted, Base64.DEFAULT)
    } catch (e: Exception) {
        null
    }
}

/**
 * Decrypts a Base64 encoded string using AES/CBC/PKCS5Padding.
 * @param secretKey A 32-character string used as the key.
 * @return Decrypted string or null if decryption fails.
 */
fun String.decrypt(secretKey: String): String? {
    return try {
        val byteStr = Base64.decode(this, Base64.DEFAULT)
        val decrypted = cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr)
        String(decrypted, StandardCharsets.UTF_8)
    } catch (e: Exception) {
        null
    }
}

@Throws(Exception::class)
private fun cipher(opmode: Int, secretKey: String): Cipher {
    require(secretKey.length == 32) { "SecretKey length must be 32 characters" }
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val keySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
    val ivSpec = IvParameterSpec(secretKey.substring(0, 16).toByteArray())
    cipher.init(opmode, keySpec, ivSpec)
    return cipher
}
