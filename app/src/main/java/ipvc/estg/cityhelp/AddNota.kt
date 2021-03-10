package ipvc.estg.cityhelp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddNota : AppCompatActivity(){

    private lateinit var addTitulo: EditText
    private lateinit var addConteudo: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_nota)

        addTitulo = findViewById(R.id.add_titulo)
        addConteudo = findViewById(R.id.add_conteudo)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener{
            val replyIntent = Intent()
            if(TextUtils.isEmpty(addTitulo.text) || TextUtils.isEmpty(addConteudo.text) ){
                setResult(Activity.RESULT_CANCELED, replyIntent)
            }else{
                val titulo = addTitulo.text.toString()
                val conteudo = addConteudo.text.toString()
                replyIntent.putExtra(EXTRA_REPLY_TITULO, titulo)
                replyIntent.putExtra(EXTRA_REPLY_CONTEUDO, conteudo)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object{
        const val EXTRA_REPLY_TITULO = "com.example.android.titulo"
        const val EXTRA_REPLY_CONTEUDO = "com.example.android.conteudo"
    }

}