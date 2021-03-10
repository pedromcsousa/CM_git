package ipvc.estg.cityhelp.db

import androidx.lifecycle.LiveData
import ipvc.estg.cityhelp.dao.NotaDAO
import ipvc.estg.cityhelp.entities.Nota

class NotaRepositorio(private val notaDAO: NotaDAO) {
    val allNotas: LiveData<List<Nota>> = notaDAO.getOrderedNotas()

    suspend fun insert(nota: Nota){
        notaDAO.insert(nota)
    }

    suspend fun deleteAll(){
        notaDAO.deleteAll()
    }
}