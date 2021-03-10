package ipvc.estg.cityhelp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ipvc.estg.cityhelp.entities.Nota

@Dao
interface NotaDAO {
    @Query("SELECT * from nota_table ORDER BY data ASC")
    fun getOrderedNotas(): LiveData<List<Nota>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(nota: Nota)

    @Query("DELETE FROM nota_table")
    suspend fun deleteAll()
}