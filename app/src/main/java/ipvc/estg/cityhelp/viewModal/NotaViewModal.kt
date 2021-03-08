package ipvc.estg.cityhelp.viewModal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ipvc.estg.cityhelp.db.NotaDB
import ipvc.estg.cityhelp.db.NotaRepositorio
import ipvc.estg.cityhelp.data.Nota
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotaViewModal(application: Application) : AndroidViewModel(application) {

    private val repository: NotaRepositorio

    val allNotas: LiveData<List<Nota>>

    init {
        val notasDao = NotaDB.getDatabase(application, viewModelScope).NotaDao()
        repository = NotaRepositorio(notasDao)
        allNotas = repository.allNotas
    }

    fun insert(nota: Nota) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(nota)
    }

}