package com.mena97villalobos.taxitiempos.database

import androidx.room.*
import com.mena97villalobos.taxitiempos.database.model.AvailableNumbers
import com.mena97villalobos.taxitiempos.database.model.Tiempo

@Dao
interface DatabaseDao {

    @Insert
    fun insertTiempo(tiempo: Tiempo)

    @Delete
    fun deleteTiempo(tiempo: Tiempo)

    @Update
    fun updateTiempo(tiempo: Tiempo)

    @Insert
    fun insertAvailableNumber(number: AvailableNumbers)

    @Delete
    fun deleteAvailableNumber(number: AvailableNumbers)

    @Update
    fun updateAvailableNumbers(number: AvailableNumbers)

    @Query("SELECT * FROM tiempos_table WHERE is_winner = 1 AND fecha_sorteo = :fecha AND is_duirna = :isDiurna;")
    fun getWinner(fecha: String, isDiurna: Boolean): List<Tiempo>

    @Query("UPDATE tiempos_table SET is_winner = 1 WHERE numero = :winningNumber AND fecha_sorteo = :fecha AND is_duirna = :isDiurna;")
    fun selectWinners(winningNumber: Int, fecha: String, isDiurna: Boolean)

    @Query("SELECT * FROM tiempos_table WHERE nombre_comprador = :nombreComprador;")
    fun getByComprador(nombreComprador: String): List<Tiempo>

    @Query("UPDATE available_numbers SET available = 10000;")
    fun resetAvailableNumbers()

    @Query("SELECT * FROM available_numbers;")
    fun getAllAvailableNumbers(): List<AvailableNumbers>

    @Query("DELETE FROM available_numbers;")
    fun deleteAllAvailableNumbers()

    @Query("SELECT * FROM available_numbers WHERE number = :number AND isDiurna = :isDiurna;")
    fun getDataByNumber(number: Int, isDiurna: Boolean): AvailableNumbers

    @Query("SELECT * FROM tiempos_table WHERE fecha_sorteo = :date;")
    fun getByDate(date: String): List<Tiempo>

    @Query("SELECT * FROM tiempos_table WHERE secret_key = :uuid;")
    fun getTiempoBySecretKey(uuid: String): Tiempo

}