package avill.ladv.chordo.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    /**
     * One-shot check for internet availability.
     */
    fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Reactive flow that emits true when internet is available and false otherwise.
     */
    val isNetworkAvailableFlow: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(isNetworkAvailable())
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(isNetworkAvailable())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(request, callback)
        trySend(isNetworkAvailable())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    /**
     * Returns detailed information about the current Wi-Fi connection.
     */
    fun getWifiInfo(): Map<String, String?> {
        val info = wifiManager.connectionInfo
        return mapOf(
            "SSID" to info.ssid?.replace("\"", ""),
            "BSSID" to info.bssid,
            "IP" to getLocalIpAddress(),
            "LinkSpeed" to "${info.linkSpeed} Mbps",
            "RSSI" to "${info.rssi} dBm",
            "Frequency" to "${info.frequency} MHz"
        )
    }

    /**
     * Gets the local IP address safely across different Android versions.
     */
    fun getLocalIpAddress(): String? {
        return try {
            Collections.list(NetworkInterface.getNetworkInterfaces()).flatMap { 
                Collections.list(it.inetAddresses) 
            }.firstOrNull { 
                !it.isLoopbackAddress && it is InetAddress && it.hostAddress?.contains(':') == false 
            }?.hostAddress
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Categorizes the current connection quality.
     */
    fun getConnectionQuality(): ConnectionQuality {
        val network = connectivityManager.activeNetwork ?: return ConnectionQuality.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return ConnectionQuality.NONE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                val rssi = wifiManager.connectionInfo.rssi
                when {
                    rssi > -50 -> ConnectionQuality.EXCELLENT
                    rssi > -70 -> ConnectionQuality.GOOD
                    else -> ConnectionQuality.POOR
                }
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                // Note: Actual speed check via NetworkCapabilities is more accurate on API 21+
                val kbps = capabilities.linkDownstreamBandwidthKbps
                when {
                    kbps > 15000 -> ConnectionQuality.EXCELLENT
                    kbps > 5000 -> ConnectionQuality.GOOD
                    else -> ConnectionQuality.POOR
                }
            }
            else -> ConnectionQuality.UNKNOWN
        }
    }

    enum class ConnectionQuality {
        EXCELLENT, GOOD, POOR, UNKNOWN, NONE
    }
}
