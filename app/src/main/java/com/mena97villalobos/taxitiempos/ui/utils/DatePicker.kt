package com.mena97villalobos.taxitiempos.ui.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class DatePicker : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val SELECTED_DATE = "SELECTED_DATE_KEY"

        fun dateToString(date: Date) = SimpleDateFormat("dd/MM/yyy", Locale.getDefault()).format(date) ?: ""

        fun stringToDate(string: String) = SimpleDateFormat("dd/MM/yyy", Locale.getDefault()).parse(string) ?: Date()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: android.widget.DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val intent = Intent()
        intent.putExtra(SELECTED_DATE, "$dayOfMonth/${month + 1}/$year")
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }
}