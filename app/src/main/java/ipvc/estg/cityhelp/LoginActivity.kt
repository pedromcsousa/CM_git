package ipvc.estg.cityhelp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ipvc.estg.cityhelp.api.EndPoints
import ipvc.estg.cityhelp.api.OutputGeral
import ipvc.estg.cityhelp.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class LoginActivity: AppCompatActivity(){

    private lateinit var user: EditText
    private lateinit var pass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref : SharedPreferences = getSharedPreferences(
            getString(R.string.sp_file),
            Context.MODE_PRIVATE
        )

        val logado = sharedPref.getBoolean(getString(R.string.logado), false)

        if(logado){
            val intent = Intent(this, MainActivity::class.java)
            Toast.makeText(this@LoginActivity, "Bem Vindo de volta", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }

        supportActionBar!!.hide()
        setContentView(R.layout.activity_login)

        user = findViewById(R.id.utilizador)
        pass = findViewById(R.id.palavraPasse)

        val entrarNotasIMG = findViewById(R.id.notas) as ImageView

        entrarNotasIMG.setOnClickListener  {
            entrarNotas()
        }
    }

    fun entrarNotas() {
        val intent = Intent(this, NotasActivity::class.java)
        startActivity(intent)
    }

    fun entrar(view: View) {
        var request = ServiceBuilder.buildServer(EndPoints::class.java)
        val call = request.login(user.text.toString(), pass.text.toString())
        val intent = Intent(this, MainActivity::class.java)

        call.enqueue(object : Callback<OutputGeral>{
            override fun onResponse(call: Call<OutputGeral>, response: Response<OutputGeral>){
                if(response.isSuccessful){
                    val c: OutputGeral = response.body()!!
                    if(c.status){
                        val sharedPref : SharedPreferences = getSharedPreferences(
                            getString(R.string.sp_file),
                            Context.MODE_PRIVATE
                        )
                        with(sharedPref.edit()){
                            putBoolean(getString(R.string.logado), true)
                            commit()
                        }
                        startActivity(intent)
                        Toast.makeText(this@LoginActivity, "Bem Vindo", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@LoginActivity, c.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<OutputGeral>, t: Throwable){
                Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}