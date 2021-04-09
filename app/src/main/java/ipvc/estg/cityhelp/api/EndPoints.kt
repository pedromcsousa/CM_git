package ipvc.estg.cityhelp.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface EndPoints {

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("user") user: String?, @Field("pass") pass: String?): Call<OutputGeral>

    @GET("situacoes")
    fun situacoes(): Call<List<Situacao>>

}