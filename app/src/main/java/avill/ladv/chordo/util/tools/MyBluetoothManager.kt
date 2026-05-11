package avill.ladv.chordo.util.tools

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MyBluetoothManager {
    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val DISCOVERABLE_DURATION = 950
        const val REQUEST_DISCOVERABLE_BT = 2258
    }

    fun bluetoothExist(activity: Activity) {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(activity, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Device supports Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }

    fun enable(activity: Activity) {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
            // Bluetooth is not enabled, request to enable it
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.startActivityForResult(
                    activity,
                    enableBtIntent,
                    REQUEST_ENABLE_BT,
                    null
                )
                return
            }
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            // Bluetooth is already enabled
            Toast.makeText(
                activity.applicationContext,
                "Bluetooth is already enabled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun disable(activity: Activity) {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            // Bluetooth is enabled, disable it
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                bluetoothAdapter.disable()
                return
            }
            bluetoothAdapter.disable()
            Toast.makeText(activity, "Bluetooth disabled", Toast.LENGTH_SHORT).show()
        } else {
            // Bluetooth is already disabled
            Toast.makeText(activity, "Bluetooth is already disabled", Toast.LENGTH_SHORT).show()
        }
    }

    fun state(activity: Activity): Boolean {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        val isEnabled = bluetoothAdapter?.isEnabled ?: false
        Log.d("MyBluetoothManager", "Bluetooth is ${if (isEnabled) "On" else "Off"}")
        return isEnabled
    }

    fun discoverableIntent(activity: Activity) {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(
            BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
            DISCOVERABLE_DURATION
        )
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.startActivityForResult(
                activity,
                discoverableIntent,
                REQUEST_DISCOVERABLE_BT,
                null
            )
            return
        }
        activity.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT)
    }

    fun bondedDevices(activity: Activity) {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            getBondedDevices(bluetoothAdapter)
        }
    }

    fun getBondedDevices(bluetoothAdapter: BluetoothAdapter) {
        @SuppressLint("MissingPermission") val pairedDevices = bluetoothAdapter.bondedDevices
        if (pairedDevices.size > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (device in pairedDevices) {
                @SuppressLint("MissingPermission") val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                Log.d(
                    MyBluetoothManager::class.java.simpleName,
                    "deviceName: $deviceName\ndeviceHardwareAddress: $deviceHardwareAddress"
                )
            }
        }
    }

    fun checkChangeState(activity: Activity) {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        activity.registerReceiver(receiver, filter)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = it.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    when (state) {
                        BluetoothAdapter.STATE_OFF -> Log.d("MyBluetoothManager", "Bluetooth Off")
                        BluetoothAdapter.STATE_TURNING_OFF -> Log.d(
                            "MyBluetoothManager",
                            "Bluetooth Turning Off"
                        )

                        BluetoothAdapter.STATE_ON -> Log.d("MyBluetoothManager", "Bluetooth On")
                        BluetoothAdapter.STATE_TURNING_ON -> Log.d(
                            "MyBluetoothManager",
                            "Bluetooth Turning On"
                        )
                    }
                }
            }
        }
    }

    fun bluetoothScanning(context: Context, activity: Activity) {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(mReceiver, filter)
        val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                    33333
                )
            }
            return
        }
        mBluetoothAdapter?.startDiscovery()
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                @SuppressLint("MissingPermission") val deviceName = device!!.name
                val deviceHardwareAddress = device.address // MAC address
                Log.i("Device Name: ", "device $deviceName")
                Log.i("deviceHardwareAddress ", "hard$deviceHardwareAddress")
            }
        }
    }
}
