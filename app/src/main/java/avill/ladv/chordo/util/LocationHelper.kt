package avill.ladv.chordo.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Modern Location Helper using FusedLocationProviderClient and Kotlin Flows.
 */

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val time: Long = 0L
) {
    fun getFormattedLocation(): String = "Lat: $latitude, Long: $longitude"
}

@Singleton
class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        var location: LocationData? = null
        val latitude: Double get() = location?.latitude ?: 0.0
        val longitude: Double get() = location?.longitude ?: 0.0
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * Checks if GPS or Network location providers are enabled.
     */
    fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Gets the last known location as a one-shot operation.
     */
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(onSuccess: (LocationData?) -> Unit) {
        if (!context.hasLocationPermission()) {
            onSuccess(null)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            onSuccess(location?.toLocationData())
        }
    }

    /**
     * Provides a Flow of location updates.
     */
    @SuppressLint("MissingPermission")
    fun getLocationUpdates(intervalMillis: Long = 5000): Flow<LocationData> = callbackFlow {
        if (!context.hasLocationPermission()) {
            close(SecurityException("Location permission missing"))
            return@callbackFlow
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis)
            .setMinUpdateIntervalMillis(intervalMillis / 2)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    val data = loc.toLocationData()
                    location = data
                    trySend(data)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    /**
     * Reverse geocoding to get a readable address.
     */
    fun reverseGeocode(latitude: Double, longitude: Double, onResult: (String) -> Unit) {
        val geocoder = Geocoder(context, Locale.getDefault())
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: List<Address>) {
                    onResult(formatAddress(addresses))
                }
                override fun onError(errorMessage: String?) {
                    onResult("Error: ${errorMessage ?: "Unknown"}")
                }
            })
        } else {
            @Suppress("DEPRECATION")
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                onResult(formatAddress(addresses))
            } catch (e: Exception) {
                onResult("No Address Found")
            }
        }
    }

    /**
     * Shows a dialog to request the user to enable GPS if it's disabled.
     */
    fun requestGPSDialog(
        activity: android.app.Activity,
        actionGPSEnabledActivityResultLauncher: androidx.activity.result.ActivityResultLauncher<android.content.Intent?>
    ) {
        android.app.AlertDialog.Builder(activity)
            .setMessage("Your GPS seems to be disabled. Do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                actionGPSEnabledActivityResultLauncher.launch(android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun formatAddress(addresses: List<Address>?): String {
        if (addresses.isNullOrEmpty()) return "No Address Found"
        val address = addresses[0]
        return buildString {
            append(address.getAddressLine(0) ?: "")
            if (address.locality != null) append(", ${address.locality}")
            if (address.adminArea != null) append(", ${address.adminArea}")
            if (address.countryName != null) append(", ${address.countryName}")
        }
    }

    private fun Location.toLocationData() = LocationData(
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        accuracy = accuracy,
        time = time
    )
}
