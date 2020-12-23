package com.mena97villalobos.taxitiempos.ui.selling

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mena97villalobos.taxitiempos.R
import com.mena97villalobos.taxitiempos.authentication.BioAuthentication
import com.mena97villalobos.taxitiempos.database.AppDatabase
import com.mena97villalobos.taxitiempos.databinding.FragmentSellingBinding
import com.mena97villalobos.taxitiempos.ui.selling.adapter.NumbersAdapter
import com.mena97villalobos.taxitiempos.ui.selling.viewmodel.SellingViewModel
import com.mena97villalobos.taxitiempos.ui.selling.viewmodel.SellingViewModelFactory
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.*
import java.util.concurrent.Executor

class SellingFragment : Fragment() {

    private val authError: () -> Unit = {
        Snackbar.make(
            binding.snackbarView,
            "Error de autentication",
            Snackbar.LENGTH_SHORT
        ).show()
    }
    private val authFailed: () -> Unit = {
        Snackbar.make(
            binding.snackbarView,
            "Autenticación fallida intentelo de nuevo",
            Snackbar.LENGTH_SHORT
        ).show()
    }
    private val authSuccess: () -> Unit = {
        val name = binding.buyerNameInput.text.toString()
        if (name.isNotBlank()) {
            viewModel.sellTiempos(
                adapter.getData(),
                name,
                currentTime == TypeTiempo.DIURNA
            )
        } else {
            Snackbar.make(
                binding.snackbarView,
                "Informacion del comprador incorrecta",
                Snackbar.LENGTH_LONG
            ).show()
        }

    }

    private val viewModel: SellingViewModel by viewModels {
        SellingViewModelFactory(
            AppDatabase.getInstance(
                requireActivity().application
            ).databaseDao
        )
    }

    private lateinit var binding: FragmentSellingBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var adapter: NumbersAdapter
    private var currentTime = TypeTiempo.DIURNA
    private var totalPrice = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_selling,
            container,
            false
        )

        setupBiometricVariables()
        setupSellClickListener()
        setupModalityButton()
        setupRecyclerView()
        setupAddButton()
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.validNumber.observe(viewLifecycleOwner, {
            fun errorHandler() {
                Snackbar.make(
                    binding.snackbarView,
                    "Número no disponible",
                    Snackbar.LENGTH_SHORT
                ).show()
                viewModel.clearValidNumber()
            }

            if (it != null && it != NumbersAdapter.errorWantedNumber) {
                if (adapter.addItem(it)) {
                    clearNumberInputs()
                    totalPrice += it.price
                    binding.totalPrice.text = getString(R.string.total_price, totalPrice)
                    viewModel.clearValidNumber()
                } else {
                    errorHandler()
                }
            } else if (it != null && it == NumbersAdapter.errorWantedNumber) {
                errorHandler()
            }
        })

        viewModel.success.observe(viewLifecycleOwner, { secretKey ->
            if (secretKey != null) {
                findNavController().navigate(
                    SellingFragmentDirections.actionSellingFragmentToSellingDialog(
                        secretKey
                    )
                )
                clearInputs()
                viewModel.clearSuccess()
            }
        })
    }

    private fun clearInputs() {
        binding.buyerNameInput.setText("")
        binding.totalPrice.text = getString(R.string.total_price, 0)
        adapter.clearData()
        clearNumberInputs()

    }

    private fun clearNumberInputs() {
        binding.numberInput.setText("")
        binding.priceInput.setText("")
    }

    private fun setupBiometricVariables() {
        executor = ContextCompat.getMainExecutor(context)
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            BioAuthentication(authError, authSuccess, authFailed)
        )


        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autorización requerida")
            .setSubtitle("Use su huella digital")
            .setNegativeButtonText("Cancelar")
            .build()
    }

    private fun setupSellClickListener() {
        binding.sellButton.setOnClickListener {
            if (
                (currentTime == TypeTiempo.DIURNA && getTimeDifferenceDiurna() > 0) ||
                (currentTime == TypeTiempo.NOCTURNA && getTimeDifferenceNocturna() > 0)
            ) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Snackbar.make(
                    binding.snackbarView,
                    "No se permite la venta de tiempos en este momentos",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupModalityButton() {
        currentTime = getModality()

        binding.modalityButton.text = getString(R.string.type_of_tiempo, currentTime.displayText)
        if (getTimeDifferenceDiurna() < 0) {
            binding.modalityButton.isEnabled = false
        }

        binding.modalityButton.setOnClickListener {
            if (getTimeDifferenceDiurna() > 0) {
                currentTime = when (currentTime) {
                    TypeTiempo.DIURNA -> TypeTiempo.NOCTURNA
                    TypeTiempo.NOCTURNA -> TypeTiempo.DIURNA
                }
                binding.modalityButton.text =
                    getString(R.string.type_of_tiempo, currentTime.displayText)
            } else if (getTimeDifferenceNocturna() > 0) {
                currentTime = TypeTiempo.NOCTURNA
                binding.modalityButton.text =
                    getString(R.string.type_of_tiempo, currentTime.displayText)
                Snackbar.make(
                    binding.snackbarView,
                    "Solo se permiten jugar tiempos nocturnos",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getModality() =
        if (getTimeDifferenceDiurna() > 0) {
            TypeTiempo.DIURNA
        } else {
            TypeTiempo.NOCTURNA
        }

    private fun setupRecyclerView() {
        adapter = NumbersAdapter(requireContext()) {
            totalPrice -= it.price
            binding.totalPrice.text = getString(R.string.total_price, totalPrice)
        }
        binding.numbersList.adapter = adapter
    }

    private fun setupAddButton() {
        binding.totalPrice.text = getString(R.string.total_price, 0)
        binding.addNumber.setOnClickListener {
            try {
                val numberText = binding.numberInput.text.toString()
                val number = (if (numberText.startsWith("0")) numberText.subSequence(1, numberText.length).toString() else numberText).toInt()
                val price = binding.priceInput.text.toString().toInt()
                val isDiurna = currentTime == TypeTiempo.DIURNA

                if (number in 0..99 && price in 100..10_000) {
                    viewModel.validateNumberPrice(number, price, isDiurna, Date())
                } else {
                    Snackbar.make(
                        binding.snackbarView,
                        "Precio o número inválido",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } catch (ignore: NumberFormatException) {
                Snackbar.make(
                    binding.snackbarView,
                    "Complete la información del tiempo",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getTimeDifferenceDiurna() =
        Duration(
            DateTime.now(),
            DateTime.now().withTimeAtStartOfDay()
                .plusHours(12)
                .plusMinutes(45)
        ).standardMinutes

    private fun getTimeDifferenceNocturna() =
        Duration(
            DateTime.now(),
            DateTime.now().withTimeAtStartOfDay()
                .plusHours(19)
                .plusMinutes(15)
        ).standardMinutes

}

enum class TypeTiempo(val displayText: String) {
    DIURNA("Diurnos"),
    NOCTURNA("Nocturnos");
}