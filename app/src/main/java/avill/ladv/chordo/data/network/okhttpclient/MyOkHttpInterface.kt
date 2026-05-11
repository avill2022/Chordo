package avill.ladv.chordo.data.network.okhttpclient

import android.util.Log
import avill.ladv.chordo.util.DatePatterns
import avill.ladv.chordo.util.Now
import com.google.gson.Gson
import org.json.JSONObject

class MyOkHttpInterface {
    private val myOkHttpClient = MyOkHttpClient()
    companion object {
        val g = Gson()
    }
    fun getCards(token:String):JSONObject{
        val response = myOkHttpClient.getCards(token)
        Log.d(MyOkHttpInterface::class.simpleName, response.toString())
        return response
    }
    @Throws(Exception::class)
    fun getRandomPuppyImage(): String {
        val response = myOkHttpClient.getRandomPuppy()
        if (response.getString("status") == "success") {
            return response.getString("message")
        } else {
            throw RuntimeException(response.getString("message"))
        }
    }

    @Throws(Exception::class)
    fun sendTransactions(json: String): Boolean {
        val response = myOkHttpClient.sendTransactions(json)
        if (response.getInt("status") == 0) {
            Log.d(MyOkHttpInterface::class.simpleName, response.getString("message"))
            return true
        } else {
            throw RuntimeException(response.getString("message"))
        }
    }

    @Throws(Exception::class)
    fun sendTransactionsTest(json: String, imei: String): Boolean {
        val response = myOkHttpClient.sendTransactionsTest(json, imei, Now.format(DatePatterns.HMS))
        if (response.getInt("status") == 0) {
            Log.d(MyOkHttpInterface::class.simpleName, response.getString("message"))
            return true
        } else {
            throw RuntimeException(response.getString("message"))
        }
    }

    @Throws(Exception::class)
    fun sendTransactionsTest2(dateYMD: String, createAt: String, json: String): Boolean {
        val response = myOkHttpClient.sendTransactionsTest2(dateYMD, createAt, json)
        if (response.getInt("status") == 0) {
            Log.d(MyOkHttpInterface::class.simpleName, response.getString("message"))
            return true
        } else {
            throw RuntimeException(response.getString("message"))
        }
    }
}
