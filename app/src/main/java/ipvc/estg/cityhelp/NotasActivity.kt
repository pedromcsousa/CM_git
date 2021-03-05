package ipvc.estg.cityhelp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog;

class NotasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        SweetAlertDialog(this@NotasActivity)
            .setTitleText("Alert Title")
            .show()
    }
}