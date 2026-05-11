package avill.ladv.chordo.viewmodel

import android.app.Activity
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import avill.ladv.chordo.data.Repository
import avill.ladv.chordo.util.DatePatterns
import avill.ladv.chordo.util.LocationHelper
import avill.ladv.chordo.util.PermissionHelper
import avill.ladv.chordo.util.hasLocationPermission
import avill.ladv.chordo.util.NetworkHelper
import avill.ladv.chordo.util.Now
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Datos(val token:String, val second:String)
class Token(val datos: Datos, var estatus: Int, val mensaje: String)
@HiltViewModel
class LoadingViewModel @Inject constructor(
    val app: Application,
    val repository: Repository,
    val locationHelper: LocationHelper,
    val permissionHelper: PermissionHelper,
    val networkHelper: NetworkHelper
) : AndroidViewModel(app) {
    companion object{
        var tokenDevice: Token = Token(Datos("",""),-1,"")
    }
    //current position
    private val _positionResult = MutableLiveData<Location>()
    val positionResult: LiveData<Location> get() = _positionResult
    fun getCurrentDevicePosition() {
        if (app.hasLocationPermission()) {
            val timer = Timer()
            val task = object : TimerTask() {
                override fun run() {
                    LocationHelper.location?.let {
                        val loc = Location("fused").apply {
                            latitude = it.latitude
                            longitude = it.longitude
                            altitude = it.altitude
                            accuracy = it.accuracy
                            time = it.time
                        }
                        _positionResult.postValue(loc)
                        timer.cancel()
                    }
                }
            }
            timer.schedule(task, 0, 1000)
        }
    }
    //token----------------------------------------------
    private val _tokenResult = MutableLiveData<Token>()
    val tokenResult: LiveData<Token> get() = _tokenResult
    fun startLoadingToken() {
        getTokenDeviceFromSharedPreferences()
        if(tokenDevice.estatus == 0) {
            _tokenResult.postValue(tokenDevice)
            Log.v(LoadingViewModel::class.java.getName(), "startLoadingToken(): already ok")
            return
        }
       /* val completableFuture = CompletableFuture.supplyAsync<Token> {
            try {
                if(networkHelper.isNetworkAvailable()){
                    repository.remoteDataSource.myOkHttpInterface.getDeviceToken()
                }else
                    Token(Datos("", ""), ERROR, "No network available")
            } catch (e: Exception) {
                Log.e(LoadingViewModel::class.java.getName(), "startLoadingToken(): error")
                sleep(1000)
                Token(Datos("", ""), ERROR, e.message.toString())
            }
        }
        Log.e(LoadingViewModel::class.java.getName(), "startLoadingToken(): whenComplete")
        completableFuture.whenComplete { result: Token, throwable: Throwable? ->
            Log.e(LoadingViewModel::class.java.getName(), "startLoadingToken(): whenComplete")
            if (throwable == null && result.estatus == OK) {
                //ok
                Log.v(LoadingViewModel::class.java.getName(), "startLoadingToken(): success")
                //set token
                tokenDevice = result
                //save token in shared preferences
                repository.getMySharedPreferences().saveString(TOKEN_DEVICE, tokenDevice.datos.token)
                //send info to the activity
                _tokenResult.postValue(tokenDevice)
            } else {
                startLoadingToken()
            }
        }*/
    }
    private fun getTokenDeviceFromSharedPreferences() {
        if (tokenDevice.estatus != 0) {
           // tokenDevice.datos.token = repository.getMySharedPreferences().getString(TOKEN_DEVICE)
        }
    }
    //cards----------------------------------------------
    private val _cardsResult = MutableLiveData<Token>()
    val cardsResult: LiveData<Token> get() = _cardsResult
    fun startLoadingCards() {
        val completableFuture = CompletableFuture.supplyAsync {
            try {
                if(tokenDevice.estatus != 0){
                    //token expired
                    //tokenDevice = repository.remoteDataSource.myOkHttpInterface.getDeviceToken()
                    //save token in shared preferences
                    repository.getMySharedPreferences().saveString("", tokenDevice.datos.token)
                    //send info to the activity
                    _tokenResult.postValue(tokenDevice)
                }
                if(tokenDevice.estatus == 0){
                    if(isSavedDateSameAsCurrent("CARDS")){
                        val jsonCards: String = repository.localDataSource.myFilesManager.getInformation("WhiteListFile")
                        createWhiteList(jsonCards)
                        true
                    }else{
                        val json: JSONObject = repository.remoteDataSource.myOkHttpInterface.getCards(
                            tokenDevice.datos.token)
                        if(json.getInt("estatus") == 0){
                            val data: String = json.getString("datos")
                            createWhiteList(data)
                            if(repository.localDataSource.myFilesManager.saveToFile(app.baseContext,
                                    "WhiteListFile", data)){
                                saveDateString("CARDS")
                            }
                            true
                        }else if(json.getInt("status") != 0){
                            //token expired
                            tokenDevice.estatus = 1
                            repository.getMySharedPreferences().saveString("", "")
                            false
                        }else
                            false
                    }
                }else false
            } catch (e: Exception) {
                false
            }
        }
        completableFuture.whenComplete { result: Boolean, throwable: Throwable? ->
            if (throwable == null && result) {
                _cardsResult.postValue(tokenDevice)
            } else {
                startLoadingCards()
            }
        }
    }
    private fun createWhiteList(jsonCards: String) {
       /* val jsonArray = JSONArray(jsonCards)
        val tempWhiteList = mutableMapOf<String, Card>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            tempWhiteList[jsonObject.getString("uid")] = Card(
                jsonObject.getInt("id"),
                jsonObject.getString("uid"),
                jsonObject.getInt("idtipotarjeta"),
                jsonObject.getLong("amount"),
                jsonObject.getString("token"),
                jsonObject.getInt("del"),
                jsonObject.getInt("social"),
                jsonObject.getString("expiracion")
            )
        }
        exampleList = tempWhiteList*/
    }
    /*fun searchCardByUid(uid: String): Card? {
        return exampleList[uid]
    }*/
    fun findCard(uid: String) {
        /*val card = searchCardByUid(uid)
        if (card != null) {
            Log.d(LoadingViewModel::class.java.getName(), "Card found: $card")
        } else {
            Log.d(LoadingViewModel::class.java.getName(), "Card not found")
        }*/
    }
    fun saveDateString(name: String) {
        repository.localDataSource.mySharedPreferences.saveString(name, Now.format(DatePatterns.HMS))
    }
    fun isSavedDateSameAsCurrent(name: String): Boolean {
        // Get the saved date from SharedPreferences
        val savedDateString: String = repository.localDataSource.mySharedPreferences.getString(name)
        // If there is no saved date, return false
        // Get the current date in the same format
        val currentDate: String = Now.format(DatePatterns.HMS)
        // Compare the saved date with the current date
        return savedDateString == currentDate
    }
    fun sleep(time: Int) {
        try {
            TimeUnit.MILLISECONDS.sleep(time.toLong())
        } catch (ignore: InterruptedException) { }
    }
}