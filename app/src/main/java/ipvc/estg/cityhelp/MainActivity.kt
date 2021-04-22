package ipvc.estg.cityhelp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import ipvc.estg.cityhelp.api.EndPoints
import ipvc.estg.cityhelp.api.OutputGeral
import ipvc.estg.cityhelp.api.ServiceBuilder
import ipvc.estg.cityhelp.api.Situacao
import ipvc.estg.cityhelp.ui.Convert
import ipvc.estg.cityhelp.ui.home.WindowInfoAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var sharedPref: SharedPreferences
    lateinit var lastLocation: Location
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var mMap: GoogleMap
    private lateinit var situacoes: List<Situacao>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //SHARED PREFERENCES
        sharedPref = getSharedPreferences(
            getString(R.string.sp_file),
            Context.MODE_PRIVATE
        )

        //MAPA
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        //"MENU" DE BOTÕES
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    public fun mapa() {

        if (this::mMap.isInitialized)
            mMap.clear()

        val sharedPref: SharedPreferences = sharedPref

        val userLogado = sharedPref.getString(getString(R.string.user), "")

        val request = ServiceBuilder.buildServer(EndPoints::class.java)
        val call = request.situacoes()
        var position = LatLng(-33.88, 151.21)

        var actividade = this
        var context = this@MainActivity

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
                                .snippet(Convert.objectToString(situacao))
                                .icon(bitmapDescriptorFromVector(context!!, icon))
                        )
                    }
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
                    mMap.setInfoWindowAdapter(WindowInfoAdapter(context))
                    mMap.setOnInfoWindowClickListener(GoogleMap.OnInfoWindowClickListener { marker ->
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
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
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

    fun entrarNotas(view: View) {
        val intent = Intent(this, NotasActivity::class.java)
        startActivity(intent)
    }

    fun sair(view: View) {
        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.sp_file),
            Context.MODE_PRIVATE
        )
        with(sharedPref.edit()) {
            putBoolean(getString(R.string.logado), false)
            commit()
        }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        setUpMap()
        mapa()
    }

    fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        } else {
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                }
            }
        }
    }

}