package ipvc.estg.cityhelp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ipvc.estg.cityhelp.entities.Nota

@Dao
interface NotaDAO {
    @Query("SELECT * from nota_table ORDER BY data DESC")
    fun getOrderedNotas(): LiveData<List<Nota>>

    @Query("SELECT * FROM nota_table WHERE id == :id")
    fun getNota(id: Int): LiveData<Nota>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(nota: Nota)

    @Query("DELETE FROM nota_table")
    suspend fun deleteAll()

    @Query("DELETE FROM nota_table WHERE id=:id")
    suspend fun delete(id: Int)

    @Query("UPDATE nota_table SET titulo=:titulo, conteudo=:conteudo, data=:data WHERE id == :id")
    suspend fun update(id: Int, titulo: String, conteudo: String, data : String)
}