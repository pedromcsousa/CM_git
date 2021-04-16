package ipvc.estg.cityhelp.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import ipvc.estg.cityhelp.R
import ipvc.estg.cityhelp.api.EndPoints
import ipvc.estg.cityhelp.api.ServiceBuilder
import ipvc.estg.cityhelp.api.Situacao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WindowInfoAdapter(mContext: Context?) : GoogleMap.InfoWindowAdapter {

    private val mWindow: View? = LayoutInflater.from(mContext).inflate(R.layout.info_situacao, null)

    private fun rendowWindowText(marker: Marker, view: View?){
        val sitTitulo = view!!.findViewById<View>(R.id.textDescSituacao) as TextView
        val sitDescricao = view.findViewById<View>(R.id.textDescSituacao) as TextView
        val sitIcons = view.findViewById<View>(R.id.iconsSituacao) as LinearLayout

        val request = ServiceBuilder.buildServer(EndPoints::class.java)
        val call = request.situacao(marker.snippet.toInt())

        var situacao : Situacao

        call.enqueue(object : Callback<Situacao> {

            override fun onResponse(call: Call<Situacao>, response: Response<Situacao>) {

                if (response.isSuccessful) {
                    situacao = response.body()!!
                    sitTitulo.text = situacao.titulo
                    sitDescricao.text = situacao.descricao
                    if(situacao.utilizador == marker.title)
                        sitIcons.visibility = View.VISIBLE
                    else
                        sitIcons.visibility = View.INVISIBLE
                    view.refreshDrawableState()
                }
            }

            override fun onFailure(call: Call<Situacao>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

}