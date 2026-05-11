package avill.ladv.chordo.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import avill.ladv.chordo.Constants

/**
 * Utility functions related to the physical device and its properties.
 */

/**
 * Returns a unique Device ID. 
 * Uses IMEI for older versions (requires permissions) or Android ID as a fallback.
 */
@SuppressLint("HardwareIds")
fun Context.getUniqueDeviceId(): String {
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val imei = try { telephonyManager.imei } catch (e: Exception) { null }
            imei ?: Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } else {
            @Suppress("DEPRECATION")
            val imei = try { telephonyManager.deviceId } catch (e: Exception) { null }
            imei ?: Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        }
    } catch (e: Exception) {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
    }
}

/**
 * Returns the application version name (e.g., "1.0.0").
 */
fun Context.getAppVersionName(): String {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionName ?: ""
    } catch (e: PackageManager.NameNotFoundException) {
        ""
    }
}

/**
 * Returns the application version code (e.g., 1).
 * Uses PackageInfoCompat for cross-version compatibility.
 */
fun Context.getAppVersionCode(): Int {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        PackageInfoCompat.getLongVersionCode(pInfo).toInt()
    } catch (e: PackageManager.NameNotFoundException) {
        0
    }
}

/**
 * Returns the WiFi MAC address. 
 * Note: On Android 6.0+, this usually returns "02:00:00:00:00:00" due to privacy restrictions.
 */
@SuppressLint("HardwareIds")
fun Context.getMacAddress(): String {
    return try {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        @Suppress("DEPRECATION")
        val wInfo: WifiInfo? = wifiManager.connectionInfo
        wInfo?.macAddress ?: "02:00:00:00:00:00"
    } catch (e: Exception) {
        "02:00:00:00:00:00"
    }
}

/**
 * Returns a human-readable device name (Manufacturer + Model).
 */
fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.lowercase().startsWith(manufacturer.lowercase())) {
        model.capitalizeWords()
    } else {
        "${manufacturer.capitalizeWords()} $model"
    }
}

/**
 * Returns the total number of cameras available on the device.
 */
fun Context.getNumberOfCameras(): Int {
    return try {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraManager.cameraIdList.size
    } catch (e: Exception) {
        0
    }
}

/**
 * Extension function to capitalize each word in a string.
 */
fun String.capitalizeWords(): String {
    if (this.isEmpty()) return ""
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}
