package ipvc.estg.cityhelp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ipvc.estg.cityhelp.adapter.NotaAdapter
import ipvc.estg.cityhelp.data.Nota
import ipvc.estg.cityhelp.viewModal.NotaViewModal

class NotasActivity : AppCompatActivity() {

    private lateinit var notas: ArrayList<Nota>
    private lateinit var notaViewModel: NotaViewModal

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
        /*notaViewModel = ViewModelProvider(this).get(NotaViewModal::class.java)
        notaViewModel.allNotas.observe(this, Observer { notas ->
            // Update the cached copy of the words in the adapter.
            notas?.let { adapter.setNotas(it) }
        })*/

        //Fab
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            /*val intent = Intent(this@NotasActivity, AddNota::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)*/
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK){
            data?.getStringExtra(AddNota.EXTRA_REPLY)?.let{
                val nota = Nota(titulo = it, conteudo = it)
                notaViewModel.insert(nota)
            }else{
                //SweetAlert Example
                SweetAlertDialog(this@NotasActivity)
                    .setTitleText("Alert Title")
                    .show()
            }
        }
    }

}