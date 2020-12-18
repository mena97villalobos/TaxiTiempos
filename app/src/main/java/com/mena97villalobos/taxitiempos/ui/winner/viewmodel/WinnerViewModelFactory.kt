package com.mena97villalobos.taxitiempos.ui.winner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mena97villalobos.taxitiempos.database.DatabaseDao

@Suppress("UNCHECKED_CAST")
class WinnerViewModelFactory (private val dataSource: DatabaseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(WinnerViewModel::class.java))
            return WinnerViewModel(dataSource) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}