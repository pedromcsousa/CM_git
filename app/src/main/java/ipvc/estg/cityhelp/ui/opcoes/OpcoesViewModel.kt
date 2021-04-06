package ipvc.estg.cityhelp.ui.opcoes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OpcoesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Opcoes Fragment"
    }
    val text: LiveData<String> = _text
}