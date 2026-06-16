package avill.ladv.chordo.util

import java.util.Locale

/**
 * Utility extensions for Hexadecimal conversions.
 */

/**
 * Converts a Hex string to a Decimal Long.
 */
fun String.hexToLong(): Long = try {
    this.toLong(16)
} catch (e: Exception) {
    0L
}

/**
 * Converts a Hex string to a Decimal Int.
 */
fun String.hexToInt(): Int = try {
    this.toInt(16)
} catch (e: Exception) {
    0
}

/**
 * Converts a Long to its Hexadecimal string representation.
 */
fun Long.toHexString(): String = java.lang.Long.toHexString(this).uppercase(Locale.getDefault())

/**
 * Converts an Int to its Hexadecimal string representation.
 */
fun Int.toHexString(): String = Integer.toHexString(this).uppercase(Locale.getDefault())

/**
 * Pads a string with leading zeros until it reaches the desired [length].
 */
fun String.padLeftZeros(length: Int): String = this.padStart(length, '0')

/**
 * Removes leading zeros from a string.
 */
fun String.removeLeadingZeros(): String = this.dropWhile { it == '0' }.ifEmpty { "0" }

/**
 * Converts a ByteArray to a Hexadecimal string.
 */
fun ByteArray.toHexString(): String {
    return joinToString("") { 
        "%02X".format(it) 
    }
}
