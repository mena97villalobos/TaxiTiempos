package com.mena97villalobos.taxitiempos.ui.selling.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.taxitiempos.database.DatabaseDao
import com.mena97villalobos.taxitiempos.database.model.Tiempo
import com.mena97villalobos.taxitiempos.ui.selling.adapter.NumbersAdapter
import com.mena97villalobos.taxitiempos.ui.selling.adapter.WantedNumber
import com.mena97villalobos.taxitiempos.ui.utils.DatePicker.Companion.dateToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SellingViewModel(private val database: DatabaseDao) : ViewModel() {

    private val _success = MutableLiveData<String>(null)
    val success: LiveData<String>
        get() = _success

    private val _validNumber = MutableLiveData<WantedNumber?>(null)
    val validNumber: LiveData<WantedNumber?>
        get() = _validNumber

    fun validateNumberPrice(number: Int, price: Int, isDiurna: Boolean, date: Date) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val priceAvailability =
                    database.getNumberTotalSell(number, isDiurna, dateToString(date))
                _validNumber.postValue(
                    if (priceAvailability.totalSells + price < 10_000) WantedNumber(number, price)
                    else NumbersAdapter.errorWantedNumber
                )
            }
        }

    fun clearValidNumber() {
        _validNumber.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }

    fun sellTiempos(
        tiempos: List<WantedNumber>,
        buyersName: String,
        buyersPhone: String,
        isDiurna: Boolean
    ) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = arrayListOf<Tiempo>()
                val secretKeyBatch = UUID.randomUUID().toString()
                tiempos.forEach {
                    val tiempo = Tiempo(
                        nombreComprador = buyersName,
                        telefonoComprador = buyersPhone,
                        isDiurna = isDiurna,
                        monto = it.price,
                        numero = it.number,
                        secretKey = secretKeyBatch
                    )
                    database.insertTiempo(tiempo)
                    result.add(tiempo)
                }
                _success.postValue(secretKeyBatch)
            }
        }

}

data class PriceAvailability(val totalSells: Int)