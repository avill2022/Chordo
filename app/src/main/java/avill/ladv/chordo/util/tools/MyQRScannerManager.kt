package avill.ladv.chordo.util.tools

import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.ScanOptions

object MyQRScannerManager {
    fun createQR(data: String, imageViewQrCode: ImageView) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400)
            imageViewQrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.d(MyQRScannerManager::class.simpleName, e.message ?: "")
        }
    }

    fun openScanner(barcodeLauncher: ActivityResultLauncher<ScanOptions>) {
        val options = ScanOptions()
            .setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
        options.setPrompt("Scan a barcode")
        options.setCameraId(0) // Use a specific camera of the device
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        barcodeLauncher.launch(options)
    }
}
