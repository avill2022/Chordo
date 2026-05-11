package avill.ladv.chordo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.nfc.NfcAdapter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class LocationModeReceiver: BroadcastReceiver() {
    companion object{
        const val GPS_STATE_CHANGED: String = "com.example.nfcstate.GPS_STATE_CHANGED"
        const val EXTRA_GPS_STATE: String = "EXTRA_GPS_STATE"
        var gpsState: Boolean = false
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action!!.matches(LocationManager.PROVIDERS_CHANGED_ACTION.toRegex())) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val localIntent = Intent(GPS_STATE_CHANGED)
            if(gpsState != isGpsEnabled){
                gpsState = isGpsEnabled
                localIntent.putExtra(EXTRA_GPS_STATE, isGpsEnabled)
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
                Log.d(LocationModeReceiver::class.java.simpleName, "Location services are enabled: $intent")

            }
        }
    }
}