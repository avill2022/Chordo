package avill.ladv.chordo.data.network.retrofit

import avill.ladv.chordo.apps.app.Chords
import retrofit2.http.GET

interface GenericApiService {
    @GET("chords.json")
    suspend fun getAll(): Chords

    /*@GET("breeds/image/random")
    suspend fun doGetListResources(): Response<MultipleResource>

    @POST("/api/users")
    suspend fun createUser(@Body user: Puppy): Response<Puppy>

    @GET("/api/users")
    suspend fun doGetUserList(@Query("page") page: String):
            Response<UserList>

    @FormUrlEncoded
    @POST("/api/users")
    suspend fun doCreateUserWithField(@Field("name") name:
                                          String, @Field("job") job: String): Response<UserList>
    @GET("search")
    suspend fun search(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("query") query: String
    ): RecipeSearchResponse

    @GET("get")
    suspend fun get(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): RecipeDto*/

}
