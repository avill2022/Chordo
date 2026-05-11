package avill.ladv.chordo.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Modern Permission Helper that works alongside ContextExtensions.
 * Focused on identifying required permissions and providing system intents.
 */
@Singleton
class PermissionHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Returns a list of required permissions that are currently denied.
     */
    fun getDeniedPermissions(permissions: Array<String>): List<String> {
        return permissions.filter { !context.hasPermission(it) }
    }

    /**
     * Checks if all provided permissions are granted.
     */
    fun hasAll(permissions: Array<String>): Boolean {
        return permissions.all { context.hasPermission(it) }
    }

    /**
     * Provides an intent to open the App Settings screen so the user can manually enable permissions.
     */
    fun getAppSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * Provides an intent to request "Manage All Files" access (Android 11+).
     */
    fun getManageAllFilesIntent(): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else null
    }

    /**
     * Provides an intent to request "Overlay" permission.
     */
    fun getOverlayIntent(): Intent {
        return Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    // Common Permission Groups
    object Groups {
        val LOCATION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        val STORAGE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}
