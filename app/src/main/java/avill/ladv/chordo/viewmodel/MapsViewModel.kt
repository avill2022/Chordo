package avill.ladv.chordo.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import avill.ladv.chordo.data.Repository
import com.google.android.gms.location.Geofence
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel@Inject constructor(
    application: Application,
    val repository: Repository
) : AndroidViewModel(application)  {
    lateinit var geofence: Geofence
    init{

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
       /* geofence.locationTest()
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
       // geofence.nextGeofence()
    }
    fun close(){
       // geofence.close()
    }
    //broadcast receiver
    private val localReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            /*val state = intent.getBooleanExtra(LocationModeReceiver.EXTRA_GPS_STATE, false)
            if (!state) {
                Toast.makeText(context, "GPS is off", Toast.LENGTH_SHORT).show()
                Log.d("GPS", "GPS is off")
            }else{
                Toast.makeText(context, "GPS is on", Toast.LENGTH_SHORT).show()
                Log.d("GPS", "GPS is on")
            }*/
        }
    }
    /*private val locationModeReceiver: LocationModeReceiver = LocationModeReceiver()
    fun registerReceiver(context: Context){
        //broadcast nfc
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(locationModeReceiver, filter)
        LocalBroadcastManager.getInstance(context).registerReceiver(
            localReceiver, IntentFilter(LocationModeReceiver.GPS_STATE_CHANGED)
        )
    }
    fun unregisterReceiver(context: Context){
        //broadcast nfc
        context.unregisterReceiver(locationModeReceiver)
        LocalBroadcastManager.getInstance(context)
            .unregisterReceiver(localReceiver)
    }*/
}