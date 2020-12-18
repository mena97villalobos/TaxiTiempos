package com.mena97villalobos.taxitiempos.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "available_numbers")
data class AvailableNumbers (
        @PrimaryKey(autoGenerate = true)
        val availableNumberId: Long = 0,

        @ColumnInfo(name = "number")
        val number: Int,

        @ColumnInfo(name = "available")
        var availableQuantity: Int,
        
        @ColumnInfo(name = "isDiurna")
        val isDiurna: Boolean = false
)