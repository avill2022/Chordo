package avill.ladv.chordo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class StartOnBootUpReceiver : BroadcastReceiver() {
    //overlay permission required
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
                //WorkerService.enqueueWork(context, new Intent());
                /*val workRequest: OneTimeWorkRequest =
                    OneTimeWorkRequest.Builder(SplashScreenWorker::class.java).build()
                WorkManager.getInstance(context!!).enqueue(workRequest)*/
            }
            Log.d(StartOnBootUpReceiver::class.java.simpleName, "StartOnBootUpReceiver")
            Toast.makeText(context, "StartOnBootUpReceiver", Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
        }
    }
}