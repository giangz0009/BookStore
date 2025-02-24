package com.pro.book.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.AddressAdapter.AddressViewHolder
import com.pro.book.model.Address

class AddressAdapter(
    private val listAddress: List<Address>?,
    private val iClickAddressListener: IClickAddressListener
) : RecyclerView.Adapter<AddressViewHolder>() {
    interface IClickAddressListener {
        fun onClickAddressItem(address: Address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = listAddress!![position]
        holder.tvName.text = address.name
        holder.tvPhone.text = address.phone
        holder.tvAddress.text = address.address
        if (address.isSelected) {
            holder.imgStatus.setImageResource(R.drawable.ic_item_selected)
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_item_unselect)
        }

        holder.layoutItem.setOnClickListener {
            iClickAddressListener.onClickAddressItem(
                address
            )
        }
    }

    override fun getItemCount(): Int {
        if (listAddress != null) {
            return listAddress.size
        }
        return 0
    }

    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgStatus: ImageView = itemView.findViewById(R.id.img_status)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvPhone: TextView = itemView.findViewById(R.id.tv_phone)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_address)
        val layoutItem: LinearLayout = itemView.findViewById(R.id.layout_item)
    }
}
