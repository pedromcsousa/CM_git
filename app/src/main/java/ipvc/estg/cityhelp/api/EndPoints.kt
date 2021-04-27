package ipvc.estg.cityhelp.api

import android.graphics.Bitmap
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("user") user: String?, @Field("pass") pass: String?): Call<OutputGeral>

    @DELETE("situacao/{id}")
    fun delSituacao(@Path("id") id: Int): Call<OutputGeral>

    @GET("situacoes")
    fun situacoes(): Call<List<Situacao>>

    @GET("situacao/{id}")
    fun situacao(@Path("id") id: Int): Call<Situacao>

    @POST("situacao")
    @Multipart
    fun addSituacao(
        @Query("titulo") titulo: String?,
        @Query("descricao") descricao: String?,
        @Query("tipo") tipo: String?,
        @Part foto: MultipartBody.Part,
        @Query("user") user: String?,
        @Query("geoX") geoX: String?,
        @Query("geoY") geoY: String?
    ): Call<OutputGeral>

    @FormUrlEncoded
    @PUT("situacao/{id}")
    fun editSituacao(
        @Path("id") id: Int,
        @Field("titulo") titulo: String?,
        @Field("descricao") descricao: String?,
        @Field("tipo") tipo: String?
    ): Call<OutputGeral>

}