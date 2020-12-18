package com.mena97villalobos.taxitiempos.ui.sellingdialog

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.mena97villalobos.taxitiempos.R
import com.mena97villalobos.taxitiempos.database.AppDatabase
import com.mena97villalobos.taxitiempos.database.model.Tiempo
import com.mena97villalobos.taxitiempos.databinding.FragmentDialogSellingBinding
import com.mena97villalobos.taxitiempos.ui.sellingdialog.viewmodel.DialogViewModel
import com.mena97villalobos.taxitiempos.ui.sellingdialog.viewmodel.DialogViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class SellingDialog : DialogFragment() {

    private lateinit var sellingDialog: Dialog
    private lateinit var binding: FragmentDialogSellingBinding
    private val args by navArgs<SellingDialogArgs>()

    private val viewModel: DialogViewModel by viewModels {
        DialogViewModelFactory(
            AppDatabase.getInstance(
                requireActivity().application
            ).databaseDao
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dialog_selling,
            container,
            false
        )
        binding.dialog = sellingDialog
        viewModel.getTiemposByIds(getIdsFromArgs())
        setupClickListener()
        setupObservers()

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sellingDialog = Dialog(requireContext())
        with(sellingDialog) {
            setContentView(R.layout.fragment_dialog_selling)
            with(window!!) {
                setGravity(Gravity.CENTER)
                setLayout(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT
                )
            }
        }
        return sellingDialog
    }

    private fun setupObservers() {
        viewModel.soldTiempos.observe(viewLifecycleOwner, {
            if (it != null) {
                setupLabels(it.first())
                val numbersString = StringBuilder()
                numbersString.append("═════════════\n")
                it.forEach { tiempo ->
                    numbersString.append("║")
                    numbersString.append("${tiempo.numero}".padStart(3, ' ').padEnd(4, ' '))
                    numbersString.append("*")
                    numbersString.append("${tiempo.monto}".padStart(6, ' ').padEnd(8, ' '))
                    numbersString.append("║\n")
                    numbersString.append("═════════════\n")
                }
                binding.numbersText.text = numbersString.toString()
                viewModel.clearTiempos()
            }
        })
    }

    private fun setupLabels(tiempo: Tiempo) {
        val isDiurnaLabel = if (tiempo.isDiurna) "Diurna" else "Nocturna"
        binding.sorteoLabel.text = getString(R.string.sorteo_label, isDiurnaLabel, 85)
        binding.buyersNameInfo.text = getString(R.string.buyers_name_info, tiempo.nombreComprador)
        binding.secretKey.text = getString(R.string.secret_key, tiempo.secretKey)
        binding.sorteoDateLabel.text = getString(
                R.string.date,
                SimpleDateFormat("dd/MM/yyy", Locale.getDefault()).format(tiempo.fechaSorteo)
        )
    }

    private fun setupClickListener() {
        binding.sendButton.setOnClickListener {
            val bitmap = Bitmap.createBitmap(
                binding.tiempoView.width,
                binding.tiempoView.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            binding.tiempoView.draw(canvas)
            val file = File(
                    requireContext().filesDir,
                    "tiempo${System.currentTimeMillis()}.jpg"
            )
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, FileOutputStream(file))
            shareImageUri(
                FileProvider.getUriForFile(
                    requireContext(),
                    "com.mena97villalobos.fileprovider",
                    file
                )
            )
        }
    }

    private fun shareImageUri(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/jpg"
        startActivity(Intent.createChooser(intent, getString(R.string.send_tiempo)))
    }

    private fun getIdsFromArgs(): List<String> {
        val listIds = arrayListOf<String>()
        args.idsList.split(",").forEach {
            listIds.add(it)
        }
        return listIds
    }

}