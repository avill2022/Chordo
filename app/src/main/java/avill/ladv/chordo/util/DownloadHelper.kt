package avill.ladv.chordo.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import avill.ladv.chordo.R
import avill.ladv.chordo.data.local.files.MyFilesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Modern Download Helper for managing file downloads and APK installations.
 */
@Singleton
class DownloadHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val downloadManager: DownloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    /**
     * Starts a download using the system DownloadManager.
     * @param url The URL to download.
     * @param fileName The name to save the file as.
     * @param title Optional title for the notification.
     */
    fun startDownload(
        url: String,
        fileName: String,
        title: String = context.getString(R.string.title_file_download)
    ): Long {
        // Clean up old file if it exists
        if (MyFilesManager.checkIfPdfExists(fileName, context, "")) {
            MyFilesManager.deleteFile(fileName, context, "")
        }

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
            .setDescription(context.getString(R.string.downloading_file))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Legacy permission check might be needed here if called from an Activity
            // but for the helper, we assume permissions are handled or use scoped storage
        }

        val downloadId = downloadManager.enqueue(request)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                createDownloadReceiver(downloadId, fileName),
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_EXPORTED
            )
        } else {
            context.registerReceiver(
                createDownloadReceiver(downloadId, fileName),
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        }
        
        return downloadId
    }

    private fun createDownloadReceiver(downloadId: Long, fileName: String) = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                Toast.makeText(context, context.getString(R.string.download_completed), Toast.LENGTH_SHORT).show()
                context.unregisterReceiver(this)
                
                if (fileName.endsWith(".apk")) {
                    installApk(fileName)
                }
            }
        }
    }

    /**
     * Triggers the Android Package Installer for a downloaded APK.
     */
    fun installApk(fileName: String) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (!file.exists()) return

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        
        context.startActivity(intent)
    }
}
