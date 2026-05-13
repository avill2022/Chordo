package avill.ladv.chordo.data.network.retrofit

import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object APIClients {
    //----------------------------------------------------------------------------------------------
    private val retrofitChordo = Retrofit.Builder()
        .baseUrl("https://avillsoftware.com/chordo/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    val chordoApiService: ChorddoApiService = retrofitChordo.create(ChorddoApiService::class.java)
    //----------------------------------------------------------------------------------------------
    private var retrofit: Retrofit? = null
    fun getGenericApiClient(baseUrl:String,service: Class<GenericApiService>): GenericApiService {
        // val interceptor = HttpLoggingInterceptor()
        // interceptor.level = HttpLoggingInterceptor.Level.BODY
        // val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .build() //client
            )
            .build()
        return retrofit!!.create(service)
    }
}
