package avill.ladv.chordo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import avill.ladv.chordo.util.LocationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {
    // Delay and update interval constants
    private val DELAY: Long = 5 * 1000
    private val UPDATE_INTERVAL: Long = 1 * 30 * 1000

    // Injected Location helper
    @Inject
    lateinit var locationHelper: LocationHelper

    private var locationJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    // Timer for periodic tasks
    private var timerServer: Timer? = null

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        
        locationJob?.cancel()
        locationJob = locationHelper.getLocationUpdates(UPDATE_INTERVAL)
            .onEach { locationData ->
                Log.i(LocationService::class.simpleName,
                    "getPositionGPS(${locationData.getFormattedLocation()})")
            }
            .launchIn(serviceScope)

        Log.i(LocationService::class.simpleName, ".onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(LocationService::class.simpleName, ".onStartCommand()")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locationJob?.cancel()
        Log.i(LocationService::class.simpleName, ".onDestroy()")
    }
}
