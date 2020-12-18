package com.mena97villalobos.taxitiempos.ui.listing.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mena97villalobos.taxitiempos.R
import com.mena97villalobos.taxitiempos.database.model.Tiempo
import com.mena97villalobos.taxitiempos.databinding.ViewListingTiempoBinding

class ListingAdapter(private val context: Context) :
        RecyclerView.Adapter<ListingAdapter.ViewHolder>() {

    private val data = arrayListOf<Tiempo>()
    private val originalData = arrayListOf<Tiempo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                    ViewListingTiempoBinding.inflate(
                            LayoutInflater.from(context),
                            parent,
                            false
                    )
            )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(data[position], context)

    override fun getItemCount(): Int = data.size

    fun addAllItems(tiempos: List<Tiempo>) {
        data.clear()
        originalData.clear()

        data.addAll(tiempos)
        originalData.addAll(tiempos)

        notifyDataSetChanged()
    }

    fun liveSearch(queryParameter: String) {
        if (queryParameter.isNotBlank()) {
            data.clear()
            data.addAll(originalData.filter { tiempo ->
                tiempo.numero.toString().contains(queryParameter) ||
                        tiempo.nombreComprador.contains(queryParameter, ignoreCase = true)
            }.toList())
        } else {
            data.clear()
            data.addAll(originalData)
        }
    }

    class ViewHolder(
            private val binding: ViewListingTiempoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tiempo: Tiempo, context: Context) {
            binding.buyerNameListing.text =
                    context.getString(R.string.buyers_name_listing, tiempo.nombreComprador)
            binding.numberListing.text = context.getString(R.string.number_listing, tiempo.numero)
            binding.priceListing.text = context.getString(R.string.price_listing, tiempo.monto)
            binding.buyerContactInfo.text = context.getString(R.string.buyers_contact_listing, tiempo.telefonoComprador)
            binding.typeImageView.setImageDrawable(ContextCompat.getDrawable(context, if (tiempo.isDiurna) R.drawable.ic_sun else R.drawable.ic_moon))
            if (tiempo.isWinner)
                binding.contents.background = ContextCompat.getDrawable(context, R.drawable.winner_box_shape)
        }

    }

}