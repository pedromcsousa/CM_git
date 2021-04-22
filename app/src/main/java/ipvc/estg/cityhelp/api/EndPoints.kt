package ipvc.estg.cityhelp.api

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

    @FormUrlEncoded
    @POST("situacao")
    fun addSituacao(@Field("titulo") titulo: String?, @Field("descricao") descricao: String?, @Field("tipo") tipo: String?, @Field("foto") foto: String?, @Field("user") user: String?, @Field("geoX") geoX: String?, @Field("geoY") geoY: String?): Call<OutputGeral>

}