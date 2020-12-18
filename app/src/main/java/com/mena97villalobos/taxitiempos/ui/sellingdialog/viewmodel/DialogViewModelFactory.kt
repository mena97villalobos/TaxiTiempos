package com.mena97villalobos.taxitiempos.ui.sellingdialog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mena97villalobos.taxitiempos.database.DatabaseDao

@Suppress("UNCHECKED_CAST")
class DialogViewModelFactory (private val dataSource: DatabaseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DialogViewModel::class.java))
            return DialogViewModel(dataSource) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}