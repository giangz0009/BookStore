package com.pro.book.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.CartAdapter.CartViewHolder
import com.pro.book.model.Product
import com.pro.book.utils.Constant
import com.pro.book.utils.GlideUtils.loadUrl

class CartAdapter(
    private val listProduct: List<Product>?,
    private val iClickCartListener: IClickCartListener
) : RecyclerView.Adapter<CartViewHolder>() {
    interface IClickCartListener {
        fun onClickDeleteItem(product: Product, position: Int)
        fun onClickUpdateItem(product: Product, position: Int)
        fun onClickEditItem(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = listProduct!![position]

        loadUrl(product.image, holder.imgProduct)
        holder.tvName.text = product.name
        val strPrice = product.priceOneProduct.toString() + Constant.CURRENCY
        holder.tvPrice.text = strPrice
        holder.tvDescription.text = product.description
        val strQuantity = "x" + product.count
        holder.tvQuantity.text = strQuantity
        holder.tvCount.text = product.count.toString()

        holder.tvSub.setOnClickListener {
            val strCount = holder.tvCount.text.toString()
            val count = strCount.toInt()
            if (count <= 1) {
                return@setOnClickListener
            }
            val newCount = count - 1
            holder.tvCount.text = newCount.toString()

            val totalPrice = product.priceOneProduct * newCount
            product.count = newCount
            product.totalPrice = totalPrice
            iClickCartListener.onClickUpdateItem(product, holder.adapterPosition)
        }

        holder.tvAdd.setOnClickListener {
            val newCount = holder.tvCount.text.toString().toInt() + 1
            holder.tvCount.text = newCount.toString()

            val totalPrice = product.priceOneProduct * newCount
            product.count = newCount
            product.totalPrice = totalPrice
            iClickCartListener.onClickUpdateItem(product, holder.adapterPosition)
        }

        holder.imgEdit.setOnClickListener { iClickCartListener.onClickEditItem(product) }
        holder.imgDelete.setOnClickListener {
            iClickCartListener.onClickDeleteItem(
                product,
                holder.adapterPosition
            )
        }
    }

    override fun getItemCount(): Int {
        if (listProduct != null) {
            return listProduct.size
        }
        return 0
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.img_product)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvQuantity: TextView = itemView.findViewById(R.id.tv_quantity)
        val tvSub: TextView = itemView.findViewById(R.id.tv_sub)
        val tvCount: TextView = itemView.findViewById(R.id.tv_count)
        val tvAdd: TextView = itemView.findViewById(R.id.tv_add)
        val imgEdit: ImageView = itemView.findViewById(R.id.img_edit)
        val imgDelete: ImageView = itemView.findViewById(R.id.img_delete)
    }
}
