package ipvc.estg.cityhelp.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.cityhelp.MainActivity
import ipvc.estg.cityhelp.R
import ipvc.estg.cityhelp.api.EndPoints
import ipvc.estg.cityhelp.api.OutputGeral
import ipvc.estg.cityhelp.api.ServiceBuilder
import ipvc.estg.cityhelp.api.Situacao
import ipvc.estg.cityhelp.ui.Convert
import ipvc.estg.cityhelp.ui.Convert.objectToString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    private lateinit var situacoes: List<Situacao>
    lateinit var mMap: GoogleMap

    val callback = OnMapReadyCallback { googleMap ->
            mMap = googleMap
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sharedPref: SharedPreferences = (this.activity as MainActivity).sharedPref

        val userLogado = sharedPref.getString(getString(R.string.user), "")

        val request = ServiceBuilder.buildServer(EndPoints::class.java)
        val call = request.situacoes()
        var position = LatLng(-33.88, 151.21)

        var actividade = this.activity as MainActivity

        call.enqueue(object : Callback<List<Situacao>> {

            override fun onResponse(
                call: Call<List<Situacao>>,
                response: Response<List<Situacao>>
            ) {
                if (response.isSuccessful) {
                    situacoes = response.body()!!
                    for (situacao in situacoes) {
                        position = LatLng(
                            situacao.geoX.toDouble(),
                            situacao.geoY.toDouble()
                        )

                        var cor: Float

                        if (situacao.tipo == 1.toString())
                            cor = BitmapDescriptorFactory.HUE_RED
                        if (situacao.tipo == 2.toString())
                            cor = BitmapDescriptorFactory.HUE_YELLOW
                        else
                            cor = BitmapDescriptorFactory.HUE_BLUE

                        mMap.addMarker(
                            MarkerOptions()
                                .position(position)
                                .title(userLogado)
                                .snippet(objectToString(situacao))
                                //.snippet(situacao.id)
                                .icon(BitmapDescriptorFactory.defaultMarker(cor))
                        )
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
                    mMap.setInfoWindowAdapter(WindowInfoAdapter(this@HomeFragment.context))
                    mMap.setOnInfoWindowClickListener(OnInfoWindowClickListener { marker ->
                        var situacao: Situacao = Convert.stringToObject(marker.snippet) as Situacao
                        if (marker.title.compareTo(situacao.utilizador) == 0)
                            println("************ CLICK GERAL SITUACAO " + situacao.titulo + "  ************")
                    })
                    mMap.setOnInfoWindowLongClickListener(GoogleMap.OnInfoWindowLongClickListener { marker ->
                        var situacao: Situacao = Convert.stringToObject(marker.snippet) as Situacao
                        if (marker.title.compareTo(situacao.utilizador) == 0){
                            SweetAlertDialog(actividade)
                                .setTitleText(getString(R.string.delete_activity_confirm))
                                .setConfirmText(getString(R.string.yes))
                                .setConfirmClickListener { sDialog ->
                                    var c = request.delSituacao(situacao.id.toInt())
                                    c.enqueue(object : Callback<OutputGeral> {

                                        override fun onResponse(
                                            ca: Call<OutputGeral>,
                                            res: Response<OutputGeral>
                                        ) {
                                            if(response.isSuccessful){
                                                val c: OutputGeral = res.body()!!
                                                if(c.status){
                                                    SweetAlertDialog(actividade)
                                                        .setTitleText("Eliminado com sucesso")
                                                        .show()
                                                    marker.remove()
                                                }else{
                                                    SweetAlertDialog(actividade)
                                                        .setTitleText(c.msg)
                                                        .show()
                                                }
                                                sDialog.cancel()
                                            }else{
                                                SweetAlertDialog(actividade)
                                                    .setTitleText("Erro na eliminação")
                                                sDialog.cancel()
                                            }
                                        }
                                        override fun onFailure(call: Call<OutputGeral>, t: Throwable) {
                                            SweetAlertDialog(actividade)
                                                .setTitleText("Erro na eliminação")
                                            sDialog.cancel()
                                        }

                                    })
                                }
                                .setCancelText(getString(R.string.no))
                                .show()
                        }
                    })
                }
            }

            override fun onFailure(call: Call<List<Situacao>>, t: Throwable) {
                Toast.makeText(activity, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}