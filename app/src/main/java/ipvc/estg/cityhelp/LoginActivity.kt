package ipvc.estg.cityhelp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}