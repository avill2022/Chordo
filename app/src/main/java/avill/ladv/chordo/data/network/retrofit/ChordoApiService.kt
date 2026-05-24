package avill.ladv.chordo.data.network.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChordoApiService {
    @GET("api/get_chords.php")
    suspend fun getChords(): Response<String>

    @POST("api/save_chords.php")
    suspend fun saveChords(@Body jsonData: String): Response<String>

    @POST("api/save_chord.php")
    suspend fun saveChord(@Body jsonData: String): Response<String>
}
