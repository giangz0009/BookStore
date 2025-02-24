package com.pro.book.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.ProductAdapter.ProductViewHolder
import com.pro.book.listener.IClickProductListener
import com.pro.book.model.Product
import com.pro.book.utils.Constant
import com.pro.book.utils.GlideUtils.loadUrl

class ProductAdapter(
    private val listProduct: List<Product>?,
    private val iClickProductListener: IClickProductListener
) : RecyclerView.Adapter<ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = listProduct!![position]

        loadUrl(product.image, holder.imgProduct)
        holder.tvName.text = product.name
        holder.tvDescription.text = product.description
        holder.tvRate.text = product.rate.toString()

        if (product.sale <= 0) {
            holder.tvPrice.visibility = View.GONE
            val strPrice = product.price.toString() + Constant.CURRENCY
            holder.tvPriceSale.text = strPrice
        } else {
            holder.tvPrice.visibility = View.VISIBLE

            val strOldPrice = product.price.toString() + Constant.CURRENCY
            holder.tvPrice.text = strOldPrice
            holder.tvPrice.paintFlags = holder.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            val strRealPrice = product.realPrice.toString() + Constant.CURRENCY
            holder.tvPriceSale.text = strRealPrice
        }

        holder.layoutItem.setOnClickListener {
            iClickProductListener.onClickProductItem(
                product
            )
        }
    }

    override fun getItemCount(): Int {
        if (listProduct != null) {
            return listProduct.size
        }
        return 0
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.img_product)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        val tvPriceSale: TextView = itemView.findViewById(R.id.tv_price_sale)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvRate: TextView = itemView.findViewById(R.id.tv_rate)
        val layoutItem: LinearLayout = itemView.findViewById(R.id.layout_item)
    }
}
