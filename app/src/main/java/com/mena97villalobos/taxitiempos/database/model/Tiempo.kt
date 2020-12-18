package com.mena97villalobos.taxitiempos.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tiempos_table")
data class Tiempo(
    @PrimaryKey(autoGenerate = true)
    val tiempoId: Long = 0,

    @ColumnInfo(name = "fecha_sorteo")
    val fechaSorteo: Date = Date(),

    @ColumnInfo(name = "nombre_comprador")
    var nombreComprador: String = "",

    @ColumnInfo(name = "telefono_comprador")
    var telefonoComprador: String = "",

    @ColumnInfo(name = "is_duirna")
    val isDiurna: Boolean = true,

    @ColumnInfo(name = "monto")
    var monto: Int = 100,

    @ColumnInfo(name = "numero")
    var numero: Int = 0,

    @ColumnInfo(name = "paga")
    val paga: Int = 85,

    @ColumnInfo(name = "is_winner")
    val isWinner: Boolean = false,

    @ColumnInfo(name = "secret_key")
    val secretKey: String
)