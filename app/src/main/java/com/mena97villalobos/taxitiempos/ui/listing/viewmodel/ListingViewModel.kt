package com.mena97villalobos.taxitiempos.ui.listing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.taxitiempos.database.DatabaseDao
import com.mena97villalobos.taxitiempos.database.model.Tiempo
import com.mena97villalobos.taxitiempos.ui.utils.DatePicker.Companion.dateToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ListingViewModel(private val database: DatabaseDao) : ViewModel() {

    private val _tiemposList = MutableLiveData<List<Tiempo>>()
    val tiemposList: LiveData<List<Tiempo>>
        get() = _tiemposList


    fun getTiemposByDate(date: Date) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _tiemposList.postValue(database.getByDate(dateToString(date)))
            }
        }

}