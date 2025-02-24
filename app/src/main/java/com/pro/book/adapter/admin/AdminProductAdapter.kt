package com.pro.book.adapter.admin

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.admin.AdminProductAdapter.AdminProductViewHolder
import com.pro.book.listener.IOnAdminManagerProductListener
import com.pro.book.model.Product
import com.pro.book.utils.Constant
import com.pro.book.utils.GlideUtils.loadUrl

class AdminProductAdapter(
    private val listProduct: List<Product>?,
    private val mListener: IOnAdminManagerProductListener
) : RecyclerView.Adapter<AdminProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_product, parent, false)
        return AdminProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminProductViewHolder, position: Int) {
        val product = listProduct!![position]

        loadUrl(product.image, holder.imgProduct)
        holder.tvName.text = product.name

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
        if (product.category_id > 0) {
            holder.layoutCategory.visibility = View.VISIBLE
            holder.tvCategory.text = product.category_name
        } else {
            holder.layoutCategory.visibility = View.GONE
        }
        if (product.isFeatured) {
            holder.tvFeatured.text = "Có"
        } else {
            holder.tvFeatured.text = "Không"
        }

        holder.imgEdit.setOnClickListener { mListener.onClickUpdateProduct(product) }
        holder.imgDelete.setOnClickListener { mListener.onClickDeleteProduct(product) }
    }

    override fun getItemCount(): Int {
        if (listProduct != null) {
            return listProduct.size
        }
        return 0
    }

    class AdminProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.img_product)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        val tvPriceSale: TextView = itemView.findViewById(R.id.tv_price_sale)
        val layoutCategory: LinearLayout = itemView.findViewById(R.id.layout_category)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        val tvFeatured: TextView = itemView.findViewById(R.id.tv_featured)
        val imgEdit: ImageView = itemView.findViewById(R.id.img_edit)
        val imgDelete: ImageView = itemView.findViewById(R.id.img_delete)
    }
}
