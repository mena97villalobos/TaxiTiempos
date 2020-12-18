package com.mena97villalobos.taxitiempos.ui.winner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.taxitiempos.database.DatabaseDao
import com.mena97villalobos.taxitiempos.database.model.Tiempo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WinnerViewModel(private val database: DatabaseDao) : ViewModel() {

    private val _success = MutableLiveData<List<Tiempo>>()
    val success: LiveData<List<Tiempo>>
        get() = _success

    fun saveWinner(winnerNumber: Int, date: Date, isDiurna: Boolean) =
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val dateSorteo = SimpleDateFormat("dd/MM/yyy", Locale.getDefault()).format(date)
                    database.selectWinners(winnerNumber, dateSorteo, isDiurna)
                    _success.postValue(database.getWinner(dateSorteo, isDiurna))
                }
            }
}