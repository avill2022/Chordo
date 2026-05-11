package avill.ladv.chordo.data.network.okhttpclient

import android.util.Log
import avill.ladv.chordo.Constants.Parameters.IMEI
import avill.ladv.chordo.Constants.Parameters.LOCATION_ID
import avill.ladv.chordo.data.network.RemoteDataSource
import avill.ladv.chordo.util.LocationHelper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class MyOkHttpClient {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val urlencoded = "application/x-www-form-urlencoded"
    private val mediaType: MediaType? = urlencoded.toMediaTypeOrNull()
    private val post = "POST"

    companion object {
        private var myOkHttpClient: MyOkHttpClient? = null

        @Synchronized
        fun getInstance(): MyOkHttpClient {
            if (myOkHttpClient == null) {
                myOkHttpClient = MyOkHttpClient()
            }
            return myOkHttpClient!!
        }
    }

    private fun createRequest(myMediaType: MediaType?, content: String): RequestBody {
        return content.toRequestBody(myMediaType)
    }
    @Throws(IOException::class, JSONException::class)
    fun getDeviceToken(): JSONObject {
        val body = createRequest(mediaType, ("imei=$IMEI")+ "&lat=" + LocationHelper.latitude + "&lon=" + LocationHelper.longitude)
        val request = Request.Builder()
            .url(RemoteDataSource.chords_api + "login_bautizador")
            .method("POST", body)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", urlencoded)
            .build()
        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            Log.v(MyOkHttpClient::class.java.simpleName, "getDeviceToken() $responseBody")
            return JSONObject(responseBody)
        }
    }
    @Throws(IOException::class, JSONException::class)
    fun getCards(token:String): JSONObject {
        val body = createRequest(mediaType, "Lat=" + LocationHelper.latitude + "&Lon=" + LocationHelper.longitude)
        val request = Request.Builder()
            .url(RemoteDataSource.chords_api + "get_tarjetas")
            .method("POST", body)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", urlencoded)
            .build()
        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            Log.v(MyOkHttpClient::class.java.simpleName, "getDeviceToken() $responseBody")
            return JSONObject(responseBody)
        }
    }
    @Throws(Exception::class)
    fun getRandomPuppy(): JSONObject {
        val request = Request.Builder()
            .url(RemoteDataSource.chords_api + "breeds/image/random")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", urlencoded)
            .addHeader("Cookie", "serviciourbano_session=w3SVJpjTeaVdWHl2KT0tmxN64L2KKjtQUCGA1T26")
            .build()
        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            Log.v(MyOkHttpClient::class.java.simpleName, "getRandomPuppy() $responseBody")
            return JSONObject(responseBody)
        }
    }

    @Throws(Exception::class)
    fun get(): JSONObject {
        val content = "Lat="
        val body = createRequest(mediaType, content)
        val request = Request.Builder()
            .url(RemoteDataSource.chords_api + "get")
            .method(post, body)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", urlencoded)
            .addHeader("Cookie", "serviciourbano_session=w3SVJpjTeaVdWHl2KT0tmxN64L2KKjtQUCGA1T26")
            .build()
        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            Log.v(MyOkHttpClient::class.java.simpleName, "get() $responseBody")
            return JSONObject(responseBody)
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun sendTransactions(json: String): JSONObject {
        val content = "body=[$json]"
        val body = createRequest(mediaType, content)
        val request = Request.Builder()
            .url(RemoteDataSource.chords_api + "api/post/create.php")
            .method("POST", body)
            .addHeader("Content-Type", urlencoded)
            .build()
        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            Log.v(MyOkHttpClient::class.java.simpleName, "sendTransaction() $responseBody")
            return JSONObject(responseBody)
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun sendTransactionsTest(json: String, imei: String, date: String): JSONObject {
        val content = "body=[$json]&imei=$imei=&created_at=$date"
        val body = createRequest(mediaType, content)
        val request = Request.Builder()
            .url(RemoteDataSource.chords_api + "api/post/update.php")
            .method("POST", body)
            .addHeader("Content-Type", urlencoded)
            .build()
        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            Log.v(MyOkHttpClient::class.java.simpleName, "sendTransaction() $responseBody")
            return JSONObject(responseBody)
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun sendTransactionsTest2(dateYMD: String, createAt: String, json: String): JSONObject {
        val content = "idLocation=$LOCATION_ID&imei=$IMEI=&dateYMD=$dateYMD=&created_at=$createAt&body=$json"
        val body = createRequest(mediaType, content)
        val request = Request.Builder()
            .url(RemoteDataSource.chords_api + "api/post/update2.php")
            .method("POST", body)
            .addHeader("Content-Type", urlencoded)
            .build()
        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            Log.v(MyOkHttpClient::class.java.simpleName, "sendTransaction() $responseBody")
            return JSONObject(responseBody)
        }
    }
}
