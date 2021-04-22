package ipvc.estg.cityhelp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    lateinit var sharedPref: SharedPreferences
    lateinit var lastLocation: Location
    lateinit  var fusedLocationClient: FusedLocationProviderClient

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

    fun entrarNotas() {
        val intent = Intent(this, NotasActivity::class.java)
        startActivity(intent)
    }

    fun sair(view: View) {
        val sharedPref : SharedPreferences = getSharedPreferences(
            getString(R.string.sp_file),
            Context.MODE_PRIVATE
        )
        with(sharedPref.edit()){
            putBoolean(getString(R.string.logado), false)
            commit()
        }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}