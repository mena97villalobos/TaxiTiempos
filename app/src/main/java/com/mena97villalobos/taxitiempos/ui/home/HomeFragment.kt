package com.mena97villalobos.taxitiempos.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.mena97villalobos.taxitiempos.R
import com.mena97villalobos.taxitiempos.databinding.FragmentHomeBinding
import com.mena97villalobos.taxitiempos.worker.DatabaseCleanUpWorker
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    companion object {
        private const val EXECUTION_DELAY_HOUR = 6
    }

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )

        setupClickListeners()
        // Removing database worker enqueueResetDatabaseWork()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.sellingButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSellingFragment())
        }
        binding.listButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToListingFragment())
        }
        binding.winningButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWinnerFragment())
        }
    }

    private fun enqueueResetDatabaseWork() {
        val delay = if (DateTime.now().hourOfDay <= EXECUTION_DELAY_HOUR) {
            Duration(
                    DateTime.now(),
                    DateTime.now().withTimeAtStartOfDay().plusHours(EXECUTION_DELAY_HOUR)
            ).standardMinutes
        } else {
            Duration(
                    DateTime.now(),
                    DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(EXECUTION_DELAY_HOUR)
            ).standardMinutes
        }

        val workRequest = PeriodicWorkRequest.Builder(
                DatabaseCleanUpWorker::class.java,
                24,
                TimeUnit.HOURS,
                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                TimeUnit.MILLISECONDS
        )
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .addTag("reset_database_worker")
                .build()
        WorkManager.getInstance(requireContext())
                .enqueueUniquePeriodicWork(
                        "reset_database_worker",
                        ExistingPeriodicWorkPolicy.KEEP,
                        workRequest
                )
    }

}