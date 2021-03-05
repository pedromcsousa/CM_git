package ipvc.estg.cityhelp

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog;
import ipvc.estg.cityhelp.adapter.NotaAdapter
import ipvc.estg.cityhelp.data.Nota

class NotasActivity : AppCompatActivity() {

    private lateinit var notas: ArrayList<Nota>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_notas)

        val recycler_view = findViewById(R.id.recycler_view) as RecyclerView

        notas = ArrayList<Nota>()

        for (i in 0 until 100) {
            notas.add(Nota("Título $i", "Conteúdo $i"))
        }
        recycler_view.adapter = NotaAdapter(notas)
        recycler_view.layoutManager = LinearLayoutManager(this)

        SweetAlertDialog(this@NotasActivity)
            .setTitleText("Alert Title")
            .show()
    }

}