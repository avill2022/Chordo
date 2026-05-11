package avill.ladv.chordo.data.network

import avill.ladv.chordo.Constants.apiKey
import okhttp3.Interceptor
import okhttp3.Response

//Authorization Header
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                "Bearer ${apiKey}"
            )
            .addHeader("Accept", "application/json")
            .build()

        return chain.proceed(request)
    }
}
