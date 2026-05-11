package avill.ladv.chordo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class NFCStateReceiver: BroadcastReceiver()  {
    companion object{
        const val NFC_STATE_CHANGED: String = "com.example.nfcstate.NFC_STATE_CHANGED"
        const val EXTRA_NFC_STATE: String = "EXTRA_NFC_STATE"
    }
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED == action) {
            val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
            val localIntent = Intent(NFC_STATE_CHANGED)
            localIntent.putExtra(EXTRA_NFC_STATE, state)
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
        }
    }
    //in the activity
    //        //onResume() nfc broadcast
    //        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
    //        registerReceiver(nfcStateReceiver, filter);
    //        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver,
    //                new IntentFilter(NFCStateReceiver.NFC_STATE_CHANGED));
    //onPause()
    //        //nfc broadcast
    //        unregisterReceiver(nfcStateReceiver);
    //        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
    private val localReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(EXTRA_NFC_STATE, NfcAdapter.STATE_OFF)
            when (state) {
                NfcAdapter.STATE_OFF ->                     // NFC is disabled
                    Toast.makeText(context, "NFC is disabled", Toast.LENGTH_SHORT).show()

                NfcAdapter.STATE_TURNING_OFF -> {}
                NfcAdapter.STATE_ON ->                     // NFC is enabled
                    Toast.makeText(context, "NFC is enabled", Toast.LENGTH_SHORT).show()

                NfcAdapter.STATE_TURNING_ON -> {}
            }
        }
    }
}