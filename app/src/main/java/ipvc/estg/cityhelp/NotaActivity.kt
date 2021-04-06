package ipvc.estg.cityhelp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import ipvc.estg.cityhelp.viewModal.NotaViewModal


class NotaActivity : AppCompatActivity(){

    private lateinit var addTitulo: EditText
    private lateinit var addConteudo: EditText
    private lateinit var titulo_activity: TextView
    private lateinit var notaViewModel: NotaViewModal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_nota)
        supportActionBar!!.hide()

        addTitulo = findViewById(R.id.add_titulo)
        addConteudo = findViewById(R.id.add_conteudo)
        titulo_activity = findViewById(R.id.add_nota_titulo)
        titulo_activity.gravity = Gravity.CENTER_VERTICAL

        titulo_activity.text =  getString(R.string.add_nota);

        var closeIcon = findViewById<ImageView>(R.id.destroy_icon)
        closeIcon.setOnClickListener {
            finish()
        }

        notaViewModel = ViewModelProvider(this).get(NotaViewModal::class.java)

        val notaID = intent.getIntExtra("EXTRA_NOTA", 0);

        if(notaID !== 0){
            titulo_activity.text = getString(R.string.edit_nota);
            notaViewModel.getNota(notaID).observe(this, Observer { nota ->
                addTitulo.setText(nota.titulo);
                addConteudo.setText(nota.conteudo);
            })
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener{
            val replyIntent = Intent()
            if(TextUtils.isEmpty(addTitulo.text) || TextUtils.isEmpty(addConteudo.text) ){
                var string = "";
                if (notaID !== 0){
                    string = getString(R.string.empty_not_updated);
                }else{
                    string = getString(R.string.empty_not_inserted);
                }

                var cancel = SweetAlertDialog.OnSweetClickListener {
                    fun onClick(sDialog : SweetAlertDialog){
                        sDialog.dismissWithAnimation()
                    }
                }

                SweetAlertDialog(this@NotaActivity)
                    .setTitleText(string)
                    .setConfirmText("Ok")
                    .setConfirmClickListener(SweetAlertDialog.OnSweetClickListener {
                        setResult(Activity.RESULT_CANCELED, replyIntent)
                        finish()
                    }
                    )
                    .show()
            }else if (notaID !== 0) {
                val titulo = addTitulo.text.toString()
                val conteudo = addConteudo.text.toString()
                replyIntent.putExtra(EXTRA_REPLY_TITULO, titulo)
                replyIntent.putExtra(EXTRA_REPLY_CONTEUDO, conteudo)
                replyIntent.putExtra("EXTRA_ID", notaID)
                replyIntent.putExtra("INSERIR", false)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }else{
                val titulo = addTitulo.text.toString()
                val conteudo = addConteudo.text.toString()
                replyIntent.putExtra(EXTRA_REPLY_TITULO, titulo)
                replyIntent.putExtra(EXTRA_REPLY_CONTEUDO, conteudo)
                replyIntent.putExtra("INSERIR", true)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }
    }

    companion object{
        const val EXTRA_REPLY_TITULO = "com.example.android.titulo"
        const val EXTRA_REPLY_CONTEUDO = "com.example.android.conteudo"
    }

}