package com.mena97villalobos.taxitiempos.ui.listing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mena97villalobos.taxitiempos.database.DatabaseDao

@Suppress("UNCHECKED_CAST")
class ListingViewModelFactory (private val dataSource: DatabaseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ListingViewModel::class.java))
            return ListingViewModel(dataSource) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}