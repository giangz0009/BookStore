package com.pro.book.adapter.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.admin.AdminTopProductAdapter.AdminTopProductViewHolder
import com.pro.book.model.ProductOrder
import com.pro.book.utils.Constant

class AdminTopProductAdapter(private val mListProducts: List<ProductOrder>?) :
    RecyclerView.Adapter<AdminTopProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminTopProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_admin_top_product,
            parent, false
        )
        return AdminTopProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminTopProductViewHolder, position: Int) {
        val product = mListProducts!![position]
        holder.tvStt.text = (position + 1).toString()
        holder.tvProductName.text = product.name
        holder.tvQuantity.text = product.count.toString()
        val strTotalPrice = (product.price * product.count).toString() + Constant.CURRENCY
        holder.tvTotalPrice.text = strTotalPrice
    }

    override fun getItemCount(): Int {
        if (mListProducts != null) {
            return mListProducts.size
        }
        return 0
    }

    class AdminTopProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStt: TextView = itemView.findViewById(R.id.tv_stt)
        val tvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
        val tvQuantity: TextView = itemView.findViewById(R.id.tv_quantity)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.tv_total_price)
    }
}
