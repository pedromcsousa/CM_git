package ipvc.estg.cityhelp.db

import androidx.lifecycle.LiveData
import ipvc.estg.cityhelp.dao.NotaDAO
import ipvc.estg.cityhelp.entities.Nota

class NotaRepositorio(private val notaDAO: NotaDAO) {
    val allNotas: LiveData<List<Nota>> = notaDAO.getOrderedNotas()

    fun getNota(id : Int): LiveData<Nota>{
        return notaDAO.getNota(id)
    }

    suspend fun insert(nota: Nota){
        notaDAO.insert(nota)
    }

    suspend fun deleteAll(){
        notaDAO.deleteAll()
    }

    suspend fun delete(id:Int){
        notaDAO.delete(id)
    }

    suspend fun update(nota: Nota){
        nota.id?.let { notaDAO.update(it, nota.titulo, nota.conteudo, java.util.Calendar.getInstance().toString()) }
    }
}