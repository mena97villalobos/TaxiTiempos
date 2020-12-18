package com.mena97villalobos.taxitiempos.ui.winner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.mena97villalobos.taxitiempos.R
import com.mena97villalobos.taxitiempos.database.AppDatabase
import com.mena97villalobos.taxitiempos.databinding.FragmentWinnerBinding
import com.mena97villalobos.taxitiempos.ui.listing.adapter.ListingAdapter
import com.mena97villalobos.taxitiempos.ui.utils.DatePicker
import com.mena97villalobos.taxitiempos.ui.utils.DatePicker.Companion.dateToString
import com.mena97villalobos.taxitiempos.ui.utils.DatePicker.Companion.stringToDate
import com.mena97villalobos.taxitiempos.ui.winner.viewmodel.WinnerViewModel
import com.mena97villalobos.taxitiempos.ui.winner.viewmodel.WinnerViewModelFactory
import java.util.*

class WinnerFragment : Fragment() {

    companion object {
        const val DATE_PICKER_REQUEST = 5555
    }

    private lateinit var binding: FragmentWinnerBinding
    private lateinit var adapter: ListingAdapter
    private var selectedDate = Date()
    private val viewModel: WinnerViewModel by viewModels {
        WinnerViewModelFactory(
            AppDatabase.getInstance(
                requireActivity().application
            ).databaseDao
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_winner,
            container,
            false
        )

        setupAdapter()
        setupObservers()
        setupWinnerButton()
        setupDateButton()
        setupIsDiurnaCheck()

        binding.selectedDate.text = getString(R.string.sorteo_date, dateToString(selectedDate))

        return binding.root
    }

    private fun setupObservers() {
        viewModel.success.observe(viewLifecycleOwner, {
            if (it != null) adapter.addAllItems(it)
        })
    }

    private fun setupAdapter() {
        adapter = ListingAdapter(requireContext())
        binding.winnersList.adapter = adapter
    }

    private fun setupWinnerButton() {
        binding.selectWinner.setOnClickListener {
            try {
                viewModel.saveWinner(
                        binding.winnerNumber.text.toString().toInt(),
                        selectedDate,
                        binding.isDiurna.isChecked
                )
            } catch (ignored: Exception) {
                Toast.makeText(context, "Informacion incompleta", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupDateButton() {
        binding.selectedDate.setOnClickListener {
            val picker = DatePicker()
            picker.setTargetFragment(this, DATE_PICKER_REQUEST)
            picker.show(parentFragmentManager, "datePicker")
        }
    }

    private fun setupIsDiurnaCheck() {
        binding.isDiurna.setOnCheckedChangeListener { _, isChecked ->
            binding.isDiurna.text = if (isChecked) "Diurna" else "Nocturna"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == DATE_PICKER_REQUEST) {
            data?.let {
                val returnDate = it.getStringExtra(DatePicker.SELECTED_DATE) ?: ""
                binding.selectedDate.text = getString(R.string.sorteo_date, returnDate)
                selectedDate = stringToDate(returnDate)
            }
        }
    }
}