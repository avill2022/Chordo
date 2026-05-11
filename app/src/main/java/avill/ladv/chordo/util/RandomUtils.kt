package avill.ladv.chordo.util

import kotlin.random.Random

/**
 * Global helpers for generating random values.
 */

fun randomInt(min: Int, max: Int) = Random.nextInt(min, max)
fun randomBoolean() = Random.nextBoolean()
fun randomDouble() = Random.nextDouble()
fun randomLong() = Random.nextLong()

/**
 * Generates a random alphanumeric string. Useful for IDs or temp filenames.
 */
fun randomString(length: Int = 10): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

/**
 * Returns a random hex color string (e.g., #FF5733).
 */
fun randomHexColor(): String {
    val chars = "0123456789ABCDEF"
    return "#" + (1..6).map { chars.random() }.joinToString("")
}

/**
 * Extensions for Ranges and Collections
 */

fun IntRange.randomInt() = Random.nextInt(start, endInclusive + 1)

fun <T> Collection<T>.randomOrNull(): T? = if (isEmpty()) null else this.random()
