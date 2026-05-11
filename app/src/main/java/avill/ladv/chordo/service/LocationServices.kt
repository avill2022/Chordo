package avill.ladv.chordo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import avill.ladv.chordo.util.LocationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationServices : Service() {
    // Injected Location helper
    @Inject
    lateinit var locationHelper: LocationHelper

    private var locationJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()
        Log.i(LocationService::class.simpleName, ".onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(LocationService::class.simpleName, ".onStartCommand()")
        
        locationJob?.cancel()
        locationJob = locationHelper.getLocationUpdates()
            .onEach { locationData ->
                Log.i(LocationServices::class.simpleName, "Location updated: ${locationData.getFormattedLocation()}")
            }
            .launchIn(serviceScope)
            
        return START_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.i(LocationService::class.simpleName, ".onDestroy()")
        locationJob?.cancel()
    }
}
