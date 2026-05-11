package avill.ladv.chordo.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import avill.ladv.chordo.R
import avill.ladv.chordo.view.activities.MainActivity

class BatteryLowReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BATTERY_LOW == intent.action) {
            showNotification(context)
            Log.d(BatteryLowReceiver::class.java.simpleName, "ACTION_BATTERY_LOW")
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, "Battery Low", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val notificationIntent = Intent(
            context,
            MainActivity::class.java
        )
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_battery_low)
            .setContentTitle(context.getString(R.string.battery_low))
            .setContentText(context.getString(R.string.battery_low_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
        notificationManager.notify(1, builder.build())
        Log.v(BatteryLowReceiver::class.java.simpleName, "Notification sent")
    }

    companion object {
        private const val CHANNEL_ID = "battery_low_channel"
    }
}