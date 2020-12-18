package com.mena97villalobos.taxitiempos.database.converters

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    @TypeConverter
    fun fromString(value: String): Date =
        SimpleDateFormat("dd/MM/yyy", Locale.getDefault()).parse(value) ?: Date()

    @TypeConverter
    fun dateToString(date: Date): String =
        SimpleDateFormat("dd/MM/yyy", Locale.getDefault()).format(date)
}