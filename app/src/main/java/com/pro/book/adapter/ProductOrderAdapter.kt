package com.pro.book.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.ProductOrderAdapter.ProductOrderViewHolder
import com.pro.book.model.ProductOrder
import com.pro.book.utils.Constant
import com.pro.book.utils.GlideUtils.loadUrl

class ProductOrderAdapter(private val listProductOrder: List<ProductOrder>?) :
    RecyclerView.Adapter<ProductOrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_order, parent, false)
        return ProductOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductOrderViewHolder, position: Int) {
        val productOrder = listProductOrder!![position]

        loadUrl(productOrder.image, holder.imgProduct)
        holder.tvName.text = productOrder.name
        val strPrice = productOrder.price.toString() + Constant.CURRENCY
        holder.tvPrice.text = strPrice
        holder.tvDescription.text = productOrder.description
        val strCount = "x" + productOrder.count
        holder.tvCount.text = strCount
    }

    override fun getItemCount(): Int {
        if (listProductOrder != null) {
            return listProductOrder.size
        }
        return 0
    }

    class ProductOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.img_product)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        val tvCount: TextView = itemView.findViewById(R.id.tv_count)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
    }
}
