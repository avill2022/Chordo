package avill.ladv.chordo.util.tools

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import java.util.Calendar

object MyAlarmManager {
    fun setAlarm(context: Context,second:Int,requestCode:Int,cls: Class<*>) {
        //
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Check if the app can schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Request the SCHEDULE_EXACT_ALARM permission
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
        //
        try {
            val intent = Intent(context, /*AlarmReceiver::class.java*/cls)
            intent.putExtra("EXTRA_MESSAGE", "fakecallalarm")
            intent.putExtra("EXTRA_REQUEST_CODE", requestCode)
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_MUTABLE)

            // Example: Set an alarm for 10 seconds from now
            val calendar = Calendar.getInstance().apply {
                add(Calendar.SECOND, second)
            }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Handle the exception gracefully (e.g., show a message to the user)
        }
        //contacts import
    }
    fun cancelAlarm(context: Context,requestCode:Int,cls: Class<*>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, /*AlarmReceiver::class.java*/cls)
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_MUTABLE)

        // Cancel the alarm
        alarmManager.cancel(pendingIntent)
    }
}