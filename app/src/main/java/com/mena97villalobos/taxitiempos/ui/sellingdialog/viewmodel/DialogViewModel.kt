package com.mena97villalobos.taxitiempos.ui.sellingdialog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.taxitiempos.database.DatabaseDao
import com.mena97villalobos.taxitiempos.database.model.Tiempo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogViewModel(private val database: DatabaseDao) : ViewModel() {

    private val _soldTiempos = MutableLiveData<List<Tiempo>>()
    val soldTiempos: LiveData<List<Tiempo>>
        get() = _soldTiempos

    fun getTiemposByIds(ids: List<String>) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val tiempos = arrayListOf<Tiempo>()
                ids.forEach { tiempos.add(database.getTiempoBySecretKey(it)) }
                _soldTiempos.postValue(tiempos)
            }
        }

    fun clearTiempos() {
        _soldTiempos.value = null
    }
}