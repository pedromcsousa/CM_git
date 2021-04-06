package ipvc.estg.cityhelp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "nota_table")

class Nota(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name="titulo") val titulo: String,
    @ColumnInfo(name="conteudo") val conteudo: String,
    @ColumnInfo(name="data") val lastUpdate: String
)