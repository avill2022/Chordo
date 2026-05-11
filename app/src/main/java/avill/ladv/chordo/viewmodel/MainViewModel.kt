package avill.ladv.chordo.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import avill.ladv.chordo.Constants
import avill.ladv.chordo.data.Repository
import avill.ladv.chordo.data.local.files.MyFilesManager
import avill.ladv.chordo.util.DatePatterns
import avill.ladv.chordo.util.LocationHelper
import avill.ladv.chordo.util.getAppVersionCode
import avill.ladv.chordo.util.getAppVersionName
import avill.ladv.chordo.util.getMacAddress
import avill.ladv.chordo.util.getUniqueDeviceId
import avill.ladv.chordo.util.NetworkHelper
import avill.ladv.chordo.util.Now
import avill.ladv.chordo.util.isValidDate
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: Repository,
    val locationHelper: LocationHelper,
    val networkHelper: NetworkHelper
) : ViewModel(){
    //var batteryLowReceiver: BatteryLowReceiver = BatteryLowReceiver()
    init{
        //repository.localDataSource.dbTest()
    }
    @SuppressLint("HardwareIds")
    fun getInfoDevice(context: Context) {
        CompletableFuture.runAsync {
            //set the imei sw and code version
            Constants.Parameters.IMEI = context.getUniqueDeviceId()
            Constants.Parameters.SW_VERSION = context.getAppVersionName()
            Constants.Parameters.CODE_VERSION = context.getAppVersionCode()
            Constants.Parameters.MAC_ADDRESS = context.getMacAddress()
        }
        //enable the wifi
        //InternetConnectivityChecker.setWifiEnabled(context);
    }
    //data-local-files------------------------------------------------------------------------------
    fun addTransactions() {
        repository.getMyFilesManager().addInformationToTheFile(
            Now.format(DatePatterns.HMS),
            "{\"MONTO_TRANSACCION\":100,\"NUMERO_SERIE_HEX\":\"00000000C3AFA6A7\",\"CONTRACT\":\"01011EC43F0132818000000000000000213BAE10D17F0042EBC4C8B761\",\"AUTOBUS\":\"CC\",\"TIPO_TARJETA\":1,\"CONTRACT_RESTRICT_TIME\":\"00\",\"SAM_COUNTER\":\"0042EB\",\"CONTRACT_SALE_SAM\":\"AE10D17F\",\"CONTRACT_TARIFF\":1,\"CONTRACT_VALIDITY_DURATION\":63,\"CONTRACT_VALIDITY_START_DATE\":\"2020-04-17 00:00:00\",\"COUNTER_AMOUNT\":\"\",\"COUNTER_VALUE\":\"\",\"CONTADOR_VALIDACIONES\":51,\"ENVIRONMENT\":\"1484013200000001213B2F7F0000010100000000000000000000000000\",\"ENVIRONMENT_ISSUER_ID\":\"32\",\"EQUIPO\":\"\",\"TIPO_EQUIPO\":\"B\",\"LINEA\":\"AA\",\"CONTADOR_RECARGAS\":0,\"LOCATION_ID\":\"AABBCC\",\"MAC\":\"EBC0AEC423B400000000C3AFA6A7038D44C0724EE5622100002EF82EF5B368041553\",\"FECHA_HORA_TRANSACCION\":\"2023-12-29 02:51:21\",\"PURCHASE_LOG\":\"FF9C26810599C0AEC423B4000031003E8004F9\",\"EVENT_LOG\":\"0100005E030115AABBCC32C4618D00006415AABBCC32C4618D01010000\",\"LOAD_LOG\":\"267B00C000003EE4002710035DAEC40B83025BDB04F8\",\"SAM_SERIAL_HEX_ULTIMA_RECARGA\":\"AEC40B83\",\"SALDO_ANTES_TRANSACCION\":16000,\"PERFIL1\":\"0\",\"PERFIL2\":\"0\",\"PERFIL3\":\"0\",\"PROVIDER\":\"15\",\"RUTA\":\"B\",\"SAM_SERIAL_HEX\":\"AEC423B4\",\"ESTACION\":\"\",\"SALDO_DESPUES_TRANSACCION\":15900,\"SV_PROVIDER_KVC\":-64,\"ID_TRANSACCION_ORGANISMO\":0,\"TIPO_TRANSACCION\":\"03\"}"
        )
    }
    fun sendFileToTheServer(){
        CompletableFuture.runAsync {
            try {
                val a = repository.getMyFilesManager().getInformation(
                    Now.format(DatePatterns.HMS)
                )
                repository.getMyOkHttpInterface().sendTransactionsTest(a, Constants.Parameters.IMEI?:"")
            } catch (e: Exception) {
                Log.d(
                    MainViewModel::class.java.simpleName,
                    String.format("%s", e.message)
                )
            }
        }
    }
    lateinit var executorSendTransactions: ScheduledExecutorService
    fun clearOldFilesExample(context: Context) {
        repository.getMyFilesManager().addInformationToTheFile(
            Now.format(DatePatterns.HMS),
            "--------------------------------------------"
        )
        executorSendTransactions = Executors.newSingleThreadScheduledExecutor()
        val sendFileTransactionsJSON = Runnable { this.openFileAndSendToServer(context) }
        executorSendTransactions.scheduleWithFixedDelay(
            sendFileTransactionsJSON,
            0,
            (15 * 1000).toLong(),
            TimeUnit.MILLISECONDS
        )
    }
    fun openFileAndSendToServer(context: Context) {
        val files: Array<File>? = MyFilesManager.getFiles(context)
        if (files != null) {
            // Iterate through the files
            for (file in files) {
                if (file.name.isValidDate(DatePatterns.HMS) && !Now.format(DatePatterns.HMS)
                        .equals(file.name)
                ) {
                    if (Constants.DEBUGGING) Log.v(
                        MyFilesManager::class.java.simpleName,
                        file.name + " is a valid date in MM-dd-yyyy format."
                    )
                } else {
                    //if(Constants.DEBUG) Log.e(MyFilesManager.class.getSimpleName(),file.getName() + " is not a valid date in MM-dd-yyyy format.");
                }
            }
        } else {
            if (Constants.DEBUGGING) Log.e(
                MyFilesManager::class.java.simpleName,
                "No files found in the directory."
            )
        }
        if (Constants.DEBUGGING) Log.e(
            MyFilesManager::class.java.simpleName,
            "-----------------------------------------------------------------------------------."
        )
        val file: File? = MyFilesManager.getValidFiles(context)
        if (file != null) {
            var transactionsFromFile: String?
            try {
                transactionsFromFile = repository.getMyFilesManager().getInformation(
                    Now.format(DatePatterns.HMS)
                )
                if (transactionsFromFile.isNotEmpty()) {
                    // if (myOkHttpClient.sendTransactionsTest(transactionsFromFile,Constants.imei)) {
                    repository.getMyFilesManager().removeFile(file.name)
                    //}
                }
            } catch (e: java.lang.Exception) {
                if (Constants.DEBUGGING) Log.v(MainViewModel::class.java.getSimpleName(), "!" + e.message)
            }
        } else if (Constants.DEBUGGING) Log.v(
            MainViewModel::class.java.getSimpleName(),
            "there is not files to send!"
        )
    }
    fun sleep(time: Int) {
        try {
            TimeUnit.MILLISECONDS.sleep(time.toLong())
        } catch (ignore: InterruptedException) {
        }
    }
    /*fun registerBatteryLowReceiver(context: Context) {
        context.registerReceiver(batteryLowReceiver, IntentFilter(Intent.ACTION_BATTERY_LOW))
    }

    fun unregisterBatteryLowReceiver(context: Context) {
        context.unregisterReceiver(batteryLowReceiver)
    }*/
}