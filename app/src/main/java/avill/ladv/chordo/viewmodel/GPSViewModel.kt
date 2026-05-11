package avill.ladv.chordo.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import avill.ladv.chordo.data.Repository
import avill.ladv.chordo.util.LocationHelper
import avill.ladv.chordo.util.hasLocationPermission
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GPSViewModel @Inject constructor(
    application: Application,
    val repository: Repository,
    val locationHelper: LocationHelper
) : AndroidViewModel(application) {
    val app = application
   // private var geofence: Geofence
    init{
        //geofence = Geofence()
    }
    //---------------------------------Location---------------------------------
    fun isGPSEnabled() {
        //_eventStateGpsLiveData.value = locationHelper.isLocationEnabled()
    }

    fun provideLocation() {
        // Flow-based location is handled reactively, but we can trigger a manual check if needed
    }

    fun getCurrentDirection(activity: Activity) {
        // Implementation moved to LocationHelper's reactive flows or reverseGeocode
    }

    fun getFusedLocation_(context: Context) {
        if (!context.hasLocationPermission()) return
        locationHelper.getLastKnownLocation { locationData ->
            // Handle last known location if needed
        }
    }
    //---------------------------------Geofence---------------------------------
    //create a mutableLiveData to observe the result of the geofence
    private val _geofenceResult = MutableLiveData<Boolean>()
    val geofenceResult: LiveData<Boolean> get() = _geofenceResult
    //
    private val _geofenceLocation = MutableLiveData<Pair<Double,Double>>()
    val geofenceLocation: LiveData<Pair<Double, Double>> get() = _geofenceLocation
    //
    private val _geofenceIndex = MutableLiveData<Int>()
    val geofenceIndex: LiveData<Int> get() = _geofenceIndex
    @SuppressLint("SetTextI18n")
    fun geofence(){
        /*geofence.locationTest()
        geofence.start(object : Geofence.OnGeofenceListener {
            override fun onGeofenceResult(result: Boolean) {
                Log.d("Geofence","Result: $result")
                _geofenceResult.value = result
            }
            override fun onGeofenceLocation(location: Pair<Double, Double>) {
                _geofenceLocation.value = location
            }

            override fun onGeofenceLocationIndex(index: Int) {
                _geofenceIndex.value = index
            }
        })*/
    }
    fun nextGeofence() {
        //geofence.nextGeofence()
    }
    fun close(){
        //geofence.close()
    }

    fun requestGPSDialog(
        activity: Activity,
        actionGPSEnabledActivityResultLauncher: ActivityResultLauncher<Intent?>
    ) {
        locationHelper.requestGPSDialog(
            activity,
            actionGPSEnabledActivityResultLauncher
        )
    }

    fun gpsState(): Boolean {
        return locationHelper.isLocationEnabled()
    }
}