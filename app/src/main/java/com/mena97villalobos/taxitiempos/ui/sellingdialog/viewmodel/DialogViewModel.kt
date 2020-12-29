package com.mena97villalobos.taxitiempos.ui.sellingdialog.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
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
                ids.forEach { tiempos.addAll(database.getTiempoBySecretKey(it)) }
                _soldTiempos.postValue(tiempos)
            }
        }

    fun clearTiempos() {
        _soldTiempos.value = null
    }

    fun printTiempos(bitmap: Bitmap) =
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val printer = EscPosPrinter(
                    BluetoothPrintersConnections.selectFirstPaired(),
                    203,
                    48f,
                    32
                )
                printer.printFormattedText(
                    "[L]<img>${PrinterTextParserImg.bytesToHexadecimalString(
                        EscPosPrinterCommands.bitmapToBytes(
                            Bitmap.createScaledBitmap(bitmap, 384, 492, true)
                        )
                    )}</img>\n[L] \n"
                )
            }
        }
}