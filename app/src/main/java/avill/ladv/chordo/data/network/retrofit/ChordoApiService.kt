package avill.ladv.chordo.data.network.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChorddoApiService {
    @GET("apis/get_chords.php")
    suspend fun getChords(): Response<String>
    @POST("apis/save_chords.php")
    suspend fun saveChords(@Body jsonData: String): Response<String>
}