package avill.ladv.chordo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.LifecycleObserver
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

