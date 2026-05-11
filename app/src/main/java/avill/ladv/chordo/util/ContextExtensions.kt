package avill.ladv.chordo.util

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import avill.ladv.chordo.Constants.DATA_STORE_ONBOARDING

/**
 * Extension functions for Context, Activity, and Application.
 */

/**
 * Checks if the app can draw over other apps.
 */
fun Context.canDrawOverlays(): Boolean = Settings.canDrawOverlays(this)

/**
 * Checks if the app has permission to manage all files (Android 11+).
 */
fun Context.hasAllFilesPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        android.os.Environment.isExternalStorageManager()
    } else true

/**
 * Checks if the app has Phone State permission (Android 10+).
 */
fun Context.hasPhoneStatePermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        hasPermission(Manifest.permission.READ_PHONE_STATE)
    } else true

/**
 * Checks if the app has Boot Completed permission.
 */
fun Context.hasBootPermission(): Boolean =
    hasPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)

/**
 * Performs a simple vibration.
 */
fun Context.vibrate(duration: Long = 50) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(duration)
    }
}

/**
 * Checks if the app has location permissions (Both Fine and Coarse).
 */
fun Context.hasLocationPermission(): Boolean =
    hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
            hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

/**
 * Checks if the app has background location permission (Android 10+).
 */
fun Context.hasBackgroundLocationPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } else true

/**
 * Checks if the app has permission to post notifications (Android 13+).
 */
fun Context.hasNotificationPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        hasPermission(Manifest.permission.POST_NOTIFICATIONS)
    } else true

/**
 * Checks if the app has camera permission.
 */
fun Context.hasCameraPermission(): Boolean =
    hasPermission(Manifest.permission.CAMERA)

/**
 * Checks if the app has storage permission (Legacy or Media).
 */
fun Context.hasStoragePermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        hasPermission(Manifest.permission.READ_MEDIA_IMAGES) ||
                hasPermission(Manifest.permission.READ_MEDIA_VIDEO) ||
                hasPermission(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

/**
 * Checks if the app has audio recording permission.
 */
fun Context.hasAudioPermission(): Boolean =
    hasPermission(Manifest.permission.RECORD_AUDIO)

/**
 * Checks if the app has SMS permission.
 */
fun Context.hasSmsPermission(): Boolean =
    hasPermission(Manifest.permission.SEND_SMS)

/**
 * Checks if the app has Call Phone permission.
 */
fun Context.hasPhonePermission(): Boolean =
    hasPermission(Manifest.permission.CALL_PHONE)

/**
 * Checks if the app has Activity Recognition permission (Android 10+).
 */
fun Context.hasActivityRecognitionPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        hasPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    } else true

/**
 * Helper to check a specific permission string.
 */
fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

/**
 * Returns a color associated with a particular resource ID.
 */
fun Context.color(@ColorRes color: Int) = ContextCompat.getColor(this, color)

/**
 * Shows a toast message from a Context.
 */
fun Context.toast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, length).show()
}

/**
 * DataStore instance for onboarding preferences.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_ONBOARDING)

/**
 * Opens a URL in the browser.
 */
fun Context.openUrl(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast("No browser found")
    }
}

/**
 * Shares plain text via system sheet.
 */
fun Context.shareText(text: String, title: String = "Share via") {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, title))
}

/**
 * Dials a phone number (doesn't require permission).
 */
fun Context.dialNumber(phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast("No dialer app found")
    }
}

/**
 * Opens Google Maps at specific coordinates.
 */
fun Context.openGoogleMaps(latitude: Double, longitude: Double) {
    val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        openUrl("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
    }
}

/**
 * Opens app settings for the current app.
 */
fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}
