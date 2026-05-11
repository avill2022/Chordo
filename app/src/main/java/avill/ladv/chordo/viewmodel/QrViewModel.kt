package avill.ladv.chordo.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import avill.ladv.chordo.data.Repository
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrViewModel @Inject constructor(
    application: Application,
    val repository: Repository
) : AndroidViewModel(application) {
    val applicationContext: Context = application.applicationContext
    val app = application
    private val _eventBitmapLiveData = MutableLiveData<Bitmap>()
    val eventBitmapLiveData: LiveData<Bitmap> get() = _eventBitmapLiveData
    fun generateQrCode(qr:String) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(qr, BarcodeFormat.QR_CODE, 400, 400)
            _eventBitmapLiveData.value = bitmap
        } catch (e: Exception) {
           // Log.d(QRActivity::class.java.simpleName, String.format("%s", e.message))
        }
    }
    //barcodeLauncher: ActivityResultLauncher<ScanOptions>
    private val _eventQrCodeLiveData = MutableLiveData<String>()
    val eventQrCodeLiveData: LiveData<String> get() = _eventQrCodeLiveData
    private val options = ScanOptions()
    fun scannerQrCode() {
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES) //ONE_D_CODE_TYPES,QR_CODE
        options.setPrompt("Scan a barcode")
        options.setCameraId(0) // Use a specific camera of the device
        options.setBeepEnabled(true)//sound
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)
        barcodeLauncher?.launch(options)
    }
    fun setTimeOut(timeOut: Long) {
        options.setTimeout(timeOut)
    }
    fun setCustomCamera(){
       // options.setCaptureActivity(CustomScannerActivity::class.java)
    }
    private var barcodeLauncher: ActivityResultLauncher<ScanOptions>? = null
    fun registerForActivityResult(activity: AppCompatActivity) {
        barcodeLauncher = activity.registerForActivityResult(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                _eventQrCodeLiveData.value = "Cancelled"
            } else {
                _eventQrCodeLiveData.value = result.contents
            }
        }
    }
    //------Telpo QR ------
    /*var qrInstance: QRValidationInstance? = null
    fun initQr(){
        qrInstance = QRValidationInstance(app.baseContext,object : QRValidationInstance.OnQRListener {
            override fun onFoundQRResult(data: String) {
                Log.d("QR",data.toString())
                _eventQrCodeLiveData.postValue(data)
            }
            override fun onError(error: String) {
                Log.d("QR",error.toString())
                Toast.makeText(app.applicationContext,error,Toast.LENGTH_SHORT).show()
            }
        })
        qrInstance?.open()
    }
    fun close() {
        qrInstance?.close()
    }*/
}