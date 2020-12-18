package com.mena97villalobos.taxitiempos.ui.selling.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.taxitiempos.database.DatabaseDao
import com.mena97villalobos.taxitiempos.database.model.Tiempo
import com.mena97villalobos.taxitiempos.ui.selling.adapter.NumbersAdapter
import com.mena97villalobos.taxitiempos.ui.selling.adapter.WantedNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SellingViewModel(private val database: DatabaseDao) : ViewModel() {

    private val _success = MutableLiveData<List<Tiempo>>(null)
    val success: LiveData<List<Tiempo>>
        get() = _success

    private val _validNumber = MutableLiveData<WantedNumber?>(null)
    val validNumber: LiveData<WantedNumber?>
        get() = _validNumber

    fun validateNumberPrice(number: Int, price: Int, isDiurna: Boolean) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val availableNumber = database.getDataByNumber(number, isDiurna)
                if (availableNumber.availableQuantity >= price) {
                    availableNumber.availableQuantity -= price
                    database.updateAvailableNumbers(availableNumber)
                    _validNumber.postValue(WantedNumber(number, price))
                } else {
                    _validNumber.postValue(NumbersAdapter.errorWantedNumber)
                }
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
                tiempos.forEach {
                    val tiempo = Tiempo(
                        nombreComprador = buyersName,
                        telefonoComprador = buyersPhone,
                        isDiurna = isDiurna,
                        monto = it.price,
                        numero = it.number,
                        secretKey = UUID.randomUUID().toString()
                    )
                    database.insertTiempo(tiempo)
                    result.add(tiempo)
                }
                _success.postValue(result)
            }
        }

    fun deleteNumber(wantedNumber: WantedNumber, isDiurna: Boolean) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val availableNumber = database.getDataByNumber(wantedNumber.number, isDiurna)
                availableNumber.availableQuantity += wantedNumber.price
                database.updateAvailableNumbers(availableNumber)
            }
        }

}