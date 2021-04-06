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

interface ClickListenerNota {
    fun clickListenerNota(nota: Nota)
    fun longClickListenerNota(nota: Nota)
}

class NotasActivity : AppCompatActivity(), ClickListenerNota {

    private lateinit var notaViewModel: NotaViewModal
    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_notas)

        // recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = NotaAdapter(this, this)
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
            val intent = Intent(this@NotasActivity, NotaActivity::class.java)
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
            val inserir = data?.getBooleanExtra("INSERIR", true);
            if (inserir!!) {
                val ptitulo = data?.getStringExtra(NotaActivity.EXTRA_REPLY_TITULO)
                val pconteudo = data?.getStringExtra(NotaActivity.EXTRA_REPLY_CONTEUDO)

                if (ptitulo != null && pconteudo != null) {
                    val nota = Nota(
                        titulo = ptitulo,
                        conteudo = pconteudo,
                        lastUpdate = java.util.Calendar.getInstance().toString()
                    )
                    notaViewModel.insert(nota)
                } else {
                    SweetAlertDialog(this@NotasActivity)
                        .setTitleText(R.string.empty_not_inserted)
                        .show()
                }
            } else {
                val ptitulo = data?.getStringExtra(NotaActivity.EXTRA_REPLY_TITULO)
                val pconteudo = data?.getStringExtra(NotaActivity.EXTRA_REPLY_CONTEUDO)
                val pid = data?.getIntExtra("EXTRA_ID", 0)
                if (ptitulo != null && pconteudo != null && pid != 0) {
                    val nota = Nota(
                        id = pid,
                        titulo = ptitulo,
                        conteudo = pconteudo,
                        lastUpdate = java.util.Calendar.getInstance().toString()
                    )
                    notaViewModel.update(nota)
                } else {
                    SweetAlertDialog(this@NotasActivity)
                        .setTitleText(R.string.empty_not_updated)
                        .show()
                }
            }
        }
    }

    override fun clickListenerNota(nota: Nota) {

        val intent = Intent(this@NotasActivity, NotaActivity::class.java)
        intent.putExtra("EXTRA_NOTA", nota.id);
        startActivityForResult(intent, newWordActivityRequestCode)
    }

    override fun longClickListenerNota(nota: Nota) {

        SweetAlertDialog(this@NotasActivity)
            .setTitleText(getString(R.string.delete_note_confirm))
            .setConfirmText(getString(R.string.yes))
            .setConfirmClickListener { sDialog ->
                nota.id?.let { it1 -> notaViewModel.delete(it1) }
                sDialog.cancel()
            }
            .setCancelText(getString(R.string.no))
            .show()
    }

}

private fun Intent.putExtra(nota: String, nota1: Nota) {

}
