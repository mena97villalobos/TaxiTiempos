package com.mena97villalobos.taxitiempos.ui.selling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mena97villalobos.taxitiempos.database.DatabaseDao

@Suppress("UNCHECKED_CAST")
class SellingViewModelFactory (private val dataSource: DatabaseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SellingViewModel::class.java))
            return SellingViewModel(dataSource) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}