package avill.ladv.chordo.data.network.retrofit

import avill.ladv.chordo.data.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClients {
    private var retrofit: Retrofit? = null
    fun getChordsApiClient(baseUrl:String): GenericApiService {
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
        return retrofit!!.create(GenericApiService::class.java)
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
}
