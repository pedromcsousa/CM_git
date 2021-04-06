package ipvc.estg.cityhelp.api

import java.sql.Timestamp

data class Situacao(
    val id: String,
    val titulo: String,
    val descricao: String,
    val timestamp: String,
    val foto: String,
    val geoX: String,
    val geoY: String,
    val tipo: String,
    val utilizador: String
)