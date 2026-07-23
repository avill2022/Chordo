package avill.ladv.chordo

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
    }
}

