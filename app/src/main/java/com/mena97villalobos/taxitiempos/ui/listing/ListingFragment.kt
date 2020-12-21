package com.mena97villalobos.taxitiempos.ui.listing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mena97villalobos.taxitiempos.R
import com.mena97villalobos.taxitiempos.database.AppDatabase
import com.mena97villalobos.taxitiempos.databinding.FragmentListingBinding
import com.mena97villalobos.taxitiempos.ui.listing.adapter.ListingAdapter
import com.mena97villalobos.taxitiempos.ui.listing.viewmodel.ListingViewModel
import com.mena97villalobos.taxitiempos.ui.listing.viewmodel.ListingViewModelFactory
import com.mena97villalobos.taxitiempos.ui.utils.DatePicker
import com.mena97villalobos.taxitiempos.ui.utils.DatePicker.Companion.stringToDate
import java.util.*

class ListingFragment : Fragment() {

    companion object {
        const val DATE_PICKER_REQUEST = 5550
    }

    private lateinit var binding: FragmentListingBinding
    private lateinit var adapter: ListingAdapter
    private var selectedDate = Date()

    private val viewModel: ListingViewModel by viewModels {
        ListingViewModelFactory(
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
            R.layout.fragment_listing,
            container,
            false
        )

        setupAdapter()
        setupObservers()
        setupSearchButton()
        setupDatePicker()
        viewModel.getTiemposByDate(selectedDate)

        binding.datePickerButton.text =
            getString(R.string.sorteo_date, DatePicker.dateToString(selectedDate))

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == DATE_PICKER_REQUEST) {
            data?.let {
                val returnDate = it.getStringExtra(DatePicker.SELECTED_DATE) ?: ""
                binding.datePickerButton.text = getString(R.string.sorteo_date, returnDate)
                selectedDate = stringToDate(returnDate)
                viewModel.getTiemposByDate(selectedDate)
            }
        }
    }

    private fun setupObservers() {
        viewModel.tiemposList.observe(viewLifecycleOwner, {
            if (it != null) {
                adapter.addAllItems(it)
                if (it.isEmpty()) {
                    binding.listingsView.visibility = View.GONE
                    binding.errorView.visibility = View.VISIBLE
                } else {
                    binding.listingsView.visibility = View.VISIBLE
                    binding.errorView.visibility = View.GONE
                }
            }
        })
    }

    private fun setupAdapter() {
        adapter = ListingAdapter(requireContext()) {
            this@ListingFragment.findNavController()
                .navigate(ListingFragmentDirections.actionListingFragmentToSellingDialog(it.secretKey))
        }
        binding.listingsView.adapter = adapter
    }

    private fun setupSearchButton() {
        binding.searchButton.setOnClickListener {
            val queryParameter = binding.searchView.query.toString()
            adapter.liveSearch(queryParameter)
        }
    }

    private fun setupDatePicker() {
        binding.datePickerButton.setOnClickListener {
            val picker = DatePicker()
            picker.setTargetFragment(this, DATE_PICKER_REQUEST)
            picker.show(parentFragmentManager, "datePicker")
        }
    }
}