package ipvc.estg.cityhelp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import ipvc.estg.cityhelp.R
import ipvc.estg.cityhelp.api.EndPoints
import ipvc.estg.cityhelp.api.ServiceBuilder
import ipvc.estg.cityhelp.api.Situacao
import ipvc.estg.cityhelp.ui.Convert.stringToObject
import retrofit2.Call
import retrofit2.Response


class WindowInfoAdapter(mContext: Context?) : GoogleMap.InfoWindowAdapter {

    private val mWindow: View? = LayoutInflater.from(mContext).inflate(R.layout.info_situacao, null)

    private fun rendowWindowText(marker: Marker, view: View?){
        val sitImagem = view!!.findViewById<View>(R.id.imagemSituacao) as ImageView
        val sitTitulo = view!!.findViewById<View>(R.id.textTituloSituacao) as TextView
        val sitAutor = view!!.findViewById<View>(R.id.textAutorSituacao) as TextView
        val sitDescricao = view.findViewById<View>(R.id.textDescSituacao) as TextView
        val sitIconEdit = view.findViewById<View>(R.id.iconEdit) as ImageView
        var situacao : Situacao = stringToObject(marker.snippet) as Situacao

        Picasso.get()
            .load(situacao.foto)
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(R.drawable.ic_baseline_image_not_supported_24)
            .into(sitImagem, object : com.squareup.picasso.Callback {

                override fun onSuccess() {
                    if (marker != null && marker.isInfoWindowShown) {
                        marker.hideInfoWindow()
                        marker.showInfoWindow()
                    }
                }

                override fun onError(e: Exception?) {
                    println(e)
                }
            })


        val request = ServiceBuilder.buildServer(EndPoints::class.java)
        val call = request.situacao(situacao.id.toInt())

        call.enqueue(object : retrofit2.Callback<Situacao> {

            override fun onResponse(call: Call<Situacao>, response: Response<Situacao>) {

                if (response.isSuccessful) {
                    situacao = response.body()!!
                    sitTitulo.text = situacao.titulo
                    sitAutor.text = situacao.utilizador
                    sitDescricao.text = situacao.descricao
                    if(situacao.utilizador == marker.title) {
                        sitIconEdit.visibility = View.VISIBLE
                        val layoutParams =
                            LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        sitIconEdit.layoutParams = layoutParams
                    }else {
                        sitIconEdit.visibility = View.INVISIBLE
                        val layoutParams =
                            LinearLayout.LayoutParams(0, 0)
                        sitIconEdit.layoutParams = layoutParams
                    }

                    if (marker != null && marker.isInfoWindowShown() ) {
                        marker.hideInfoWindow()
                        marker.showInfoWindow();
                    }
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
        return mWindow
    }

}
