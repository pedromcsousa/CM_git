package ipvc.estg.cityhelp.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
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
        var context = this.context

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

                        var icon: Int =
                            if (situacao.utilizador == userLogado) {
                                if (situacao.tipo.compareTo("Sugestão") == 0)
                                    R.drawable.ic_baseline_highlight_24_blue
                                else if (situacao.tipo.compareTo("Evento") == 0)
                                    R.drawable.ic_baseline_event_24_blue
                                else
                                    R.drawable.ic_baseline_report_problem_24_blue
                            } else {
                                if (situacao.tipo.compareTo("Sugestão") == 0)
                                    R.drawable.ic_baseline_highlight_24
                                else if (situacao.tipo.compareTo("Evento") == 0)
                                    R.drawable.ic_baseline_event_24
                                else
                                    R.drawable.ic_baseline_report_problem_24
                            }

                        mMap.addMarker(
                            MarkerOptions()
                                .position(position)
                                .title(userLogado)
                                .snippet(objectToString(situacao))
                                .icon(bitmapDescriptorFromVector(context!!, icon))
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
                        if (marker.title.compareTo(situacao.utilizador) == 0) {
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
                                            if (response.isSuccessful) {
                                                val c: OutputGeral = res.body()!!
                                                if (c.status) {
                                                    SweetAlertDialog(actividade)
                                                        .setTitleText("Eliminado com sucesso")
                                                        .show()
                                                    marker.remove()
                                                } else {
                                                    SweetAlertDialog(actividade)
                                                        .setTitleText(c.msg)
                                                        .show()
                                                }
                                                sDialog.cancel()
                                            } else {
                                                SweetAlertDialog(actividade)
                                                    .setTitleText("Erro na eliminação")
                                                sDialog.cancel()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<OutputGeral>,
                                            t: Throwable
                                        ) {
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

    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {
        val background: Drawable =
            ContextCompat.getDrawable(context, vectorDrawableResourceId)!!
        background.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable: Drawable =
            ContextCompat.getDrawable(context, vectorDrawableResourceId)!!
        vectorDrawable.setBounds(
            40,
            20,
            vectorDrawable.intrinsicWidth + 40,
            vectorDrawable.intrinsicHeight + 20
        )
        val bitmap = Bitmap.createBitmap(
            background.intrinsicWidth,
            background.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}