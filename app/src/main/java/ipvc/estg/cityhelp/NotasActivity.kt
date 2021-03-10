package ipvc.estg.cityhelp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ipvc.estg.cityhelp.adapter.NotaAdapter
import ipvc.estg.cityhelp.entities.Nota
import ipvc.estg.cityhelp.viewModal.NotaViewModal

class NotasActivity : AppCompatActivity() {

    private lateinit var notaViewModel: NotaViewModal
    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_notas)

        // recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = NotaAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // view model
        notaViewModel = ViewModelProvider(this).get(NotaViewModal::class.java)
        notaViewModel.allNotas.observe(this, Observer { notas ->
            // Update the cached copy of the words in the adapter.
            notas?.let { adapter.setNotas(it) }
        })

        //Fab
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@NotasActivity, AddNota::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

        var closeIcon = findViewById<ImageView>(R.id.destroy_icon)
        closeIcon.setOnClickListener {
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val ptitulo = data?.getStringExtra(AddNota.EXTRA_REPLY_TITULO)
            val pconteudo = data?.getStringExtra(AddNota.EXTRA_REPLY_CONTEUDO)

            if (ptitulo != null && pconteudo != null) {
                val nota = Nota(titulo = ptitulo, conteudo = pconteudo, lastUpdate = java.util.Calendar.getInstance().toString())
                notaViewModel.insert(nota)
            }

        } else {
            SweetAlertDialog(this@NotasActivity)
                .setTitleText(R.string.empty_not_saved)
                .show()
        }
    }

}