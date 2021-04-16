package ipvc.estg.cityhelp.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.cityhelp.MainActivity
import ipvc.estg.cityhelp.R
import ipvc.estg.cityhelp.api.EndPoints
import ipvc.estg.cityhelp.api.ServiceBuilder
import ipvc.estg.cityhelp.api.Situacao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var situacoes: List<Situacao>
    private lateinit var mMap : GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        /*val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val sharedPref : SharedPreferences = (this.activity as MainActivity).sharedPref

        val userLogado = sharedPref.getString(getString(R.string.user), "")

        val request = ServiceBuilder.buildServer(EndPoints::class.java)
        val call = request.situacoes()
        var position = LatLng(-33.88, 151.21)

        call.enqueue(object : Callback<List<Situacao>> {

            override fun onResponse(
                call: Call<List<Situacao>>,
                response: Response<List<Situacao>>
            ) {
                if (response.isSuccessful) {
                    situacoes = response.body()!!
                    for(situacao in situacoes){
                        position = LatLng(
                            situacao.geoX.toDouble(),
                            situacao.geoY.toDouble()
                        )

                        var cor : Float

                        if(situacao.tipo == 1.toString())
                            cor = BitmapDescriptorFactory.HUE_RED
                        if(situacao.tipo == 2.toString())
                            cor = BitmapDescriptorFactory.HUE_YELLOW
                        else
                            cor = BitmapDescriptorFactory.HUE_BLUE

                        mMap.addMarker(MarkerOptions()
                            .position(position)
                            .title(userLogado)
                            .snippet(situacao.id)
                            .icon(BitmapDescriptorFactory.defaultMarker(cor))
                        )
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
                    mMap.setInfoWindowAdapter(WindowInfoAdapter(this@HomeFragment.context))
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