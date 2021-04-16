package ipvc.estg.cityhelp.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("user") user: String?, @Field("pass") pass: String?): Call<OutputGeral>

    @GET("situacoes")
    fun situacoes(): Call<List<Situacao>>

    @GET("situacao/{id}")
    fun situacao(@Path("id") id: Int): Call<Situacao>

}