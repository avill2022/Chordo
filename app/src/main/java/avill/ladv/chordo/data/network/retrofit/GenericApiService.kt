package avill.ladv.chordo.data.network.retrofit

import avill.ladv.chordo.apps.app.model.Chords
import retrofit2.http.GET

interface GenericApiService {
    @GET("chords.json")
    suspend fun getAll(): Chords
}
