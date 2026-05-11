package avill.ladv.chordo.util

import android.content.Context
import android.text.format.DateUtils
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Modern Date and Time utilities using java.time API.
 */

object DatePatterns {
    val YMD_HMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val YMD = DateTimeFormatter.ofPattern("yyyy:MM:dd")
    val HMS = DateTimeFormatter.ofPattern("HH:mm:ss")
    val MD_HM = DateTimeFormatter.ofPattern("MM-dd HH:mm")
    val MDY_DASH = DateTimeFormatter.ofPattern("MM-dd-yyyy")
    val MDY_SLASH = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val FULL_DATE_TIME = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
}

// --- Long (Timestamp) Extensions ---

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

fun Long.format(formatter: DateTimeFormatter): String =
    this.toLocalDateTime().format(formatter)

/**
 * Formats seconds into HH:mm:ss
 */
fun Long.secondsToHMS(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val secs = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

// --- Date Extensions (Legacy Interop) ---

fun Date.toLocalDateTime(): LocalDateTime =
    this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

/**
 * Converts Date to a relative string like "Today at 10:00 AM" or "2 days ago".
 */
fun Date.toRelativeTimeSpan(context: Context): String {
    return DateUtils.getRelativeTimeSpanString(
        this.time,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
}

fun Date.getDayNameSpanish(): String {
    val day = this.toLocalDateTime().dayOfWeek
    return when (day) {
        DayOfWeek.SUNDAY -> "Domingo"
        DayOfWeek.MONDAY -> "Lunes"
        DayOfWeek.TUESDAY -> "Martes"
        DayOfWeek.WEDNESDAY -> "Miércoles"
        DayOfWeek.THURSDAY -> "Jueves"
        DayOfWeek.FRIDAY -> "Viernes"
        DayOfWeek.SATURDAY -> "Sábado"
    }
}

fun Date.addDays(days: Long): Date =
    Date.from(this.toInstant().plus(Duration.ofDays(days)))

// --- String Extensions ---

fun String.toMillis(formatter: DateTimeFormatter): Long? = try {
    LocalDateTime.parse(this, formatter)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
} catch (e: Exception) {
    null
}

/**
 * Validates if a string matches the given format.
 */
fun String.isValidDate(formatter: DateTimeFormatter): Boolean = try {
    formatter.parse(this)
    true
} catch (e: Exception) {
    false
}

// --- Global Current Time Helpers ---

object Now {
    val millis: Long get() = System.currentTimeMillis()
    val year: Int get() = LocalDate.now().year
    val month: Int get() = LocalDate.now().monthValue
    val day: Int get() = LocalDate.now().dayOfMonth
    
    fun format(formatter: DateTimeFormatter): String =
        LocalDateTime.now().format(formatter)
}
