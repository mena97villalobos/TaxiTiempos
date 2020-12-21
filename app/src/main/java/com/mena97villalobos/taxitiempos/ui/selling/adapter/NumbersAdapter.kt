package com.mena97villalobos.taxitiempos.ui.selling.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mena97villalobos.taxitiempos.databinding.ViewSelectedNumberBinding

class NumbersAdapter(
        private val context: Context,
        private val deleteViewModelCallback: (WantedNumber) -> Unit
): RecyclerView.Adapter<NumbersAdapter.ViewHolder>() {

    companion object {
        val errorWantedNumber = WantedNumber(-1, -1)
    }

    private val data = arrayListOf<WantedNumber>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(context, ViewSelectedNumberBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            ))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position]) {
            deleteViewModelCallback(data[position])
            data.removeAt(position)
            notifyItemRemoved(position)
        }

    override fun getItemCount(): Int = data.size

    fun getData() = data

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    fun addItem(number: WantedNumber) : Boolean {
        var currentSellValue = 0
        data.filter { currentItem -> currentItem.number == number.number }
            .forEach { currentItem -> currentSellValue += currentItem.price }

        return if (currentSellValue + number.price <= 10_000) {
            data.add(0, number)
            notifyItemInserted(0)
            true
        } else {
            false
        }
    }

    class ViewHolder(
            private val context: Context,
            private val binding: ViewSelectedNumberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wantedNumber: WantedNumber, deleteClickListener: View.OnClickListener) {
            val numberText = if (wantedNumber.number <= 9) "0${wantedNumber.number}" else wantedNumber.number.toString()
            binding.number.text = numberText
            binding.deleteButton.setOnClickListener(deleteClickListener)
            binding.content.setOnClickListener {
                Toast.makeText(context, "Número $numberText Monto: ${wantedNumber.price}", Toast.LENGTH_LONG).show()
            }
        }

    }

}

data class WantedNumber(
        val number: Int,
        val price: Int
)