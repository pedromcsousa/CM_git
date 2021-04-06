package ipvc.estg.cityhelp.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()

    private  val retrofit = Retrofit.Builder()
        .baseUrl("https://satbello.com/portal/cm/cityhelp/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildServer(service: Class<T>): T{
        return retrofit.create(service)
    }
}