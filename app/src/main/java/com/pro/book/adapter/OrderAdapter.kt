package com.pro.book.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.OrderAdapter.OrderViewHolder
import com.pro.book.model.Order
import com.pro.book.prefs.DataStoreManager
import com.pro.book.utils.Constant
import com.pro.book.utils.GlideUtils.loadUrl

class OrderAdapter(
    private var context: Context?,
    private val listOrder: List<Order>?,
    private val iClickOrderListener: IClickOrderListener
) : RecyclerView.Adapter<OrderViewHolder>() {
    interface IClickOrderListener {
        fun onClickTrackingOrder(orderId: Long)
        fun onClickReceiptOrder(order: Order?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = listOrder!![position]

        if (DataStoreManager.user!!.isAdmin) {
            holder.layoutUser.visibility = View.VISIBLE
            holder.tvUser.text = order.userEmail
        } else {
            holder.layoutUser.visibility = View.GONE
        }
        val firstProductOrder = order.products!![0]
        loadUrl(firstProductOrder.image, holder.imgProduct)
        holder.tvOrderId.text = order.id.toString()
        val strTotal = order.total.toString() + Constant.CURRENCY
        holder.tvTotal.text = strTotal
        holder.tvProductsName.text = order.listProductsName
        val strQuantity =
            "(" + order.products!!.size + " " + context!!.getString(R.string.label_item) + ")"
        holder.tvQuantity.text = strQuantity

        if (Order.STATUS_COMPLETE == order.status) {
            holder.tvSuccess.visibility = View.VISIBLE
            holder.tvAction.text = context!!.getString(R.string.label_receipt_order)
            holder.layoutReview.visibility = View.VISIBLE
            holder.tvRate.text = order.rate.toString()
            holder.tvReview.text = order.review
            holder.layoutAction.setOnClickListener {
                iClickOrderListener.onClickReceiptOrder(
                    order
                )
            }
        } else {
            holder.tvSuccess.visibility = View.GONE
            holder.tvAction.text = context!!.getString(R.string.label_tracking_order)
            holder.layoutReview.visibility = View.GONE
            holder.layoutAction.setOnClickListener {
                iClickOrderListener.onClickTrackingOrder(
                    order.id
                )
            }
        }
    }

    override fun getItemCount(): Int {
        if (listOrder != null) {
            return listOrder.size
        }
        return 0
    }

    fun release() {
        if (context != null) context = null
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.img_product)
        val tvOrderId: TextView = itemView.findViewById(R.id.tv_order_id)
        val tvTotal: TextView = itemView.findViewById(R.id.tv_total)
        val tvProductsName: TextView = itemView.findViewById(R.id.tv_products_name)
        val tvQuantity: TextView = itemView.findViewById(R.id.tv_quantity)
        val tvSuccess: TextView = itemView.findViewById(R.id.tv_success)
        val layoutAction: LinearLayout = itemView.findViewById(R.id.layout_action)
        val tvAction: TextView = itemView.findViewById(R.id.tv_action)
        val layoutReview: LinearLayout = itemView.findViewById(R.id.layout_review)
        val tvRate: TextView = itemView.findViewById(R.id.tv_rate)
        val tvReview: TextView = itemView.findViewById(R.id.tv_review)
        val layoutUser: LinearLayout = itemView.findViewById(R.id.layout_user)
        val tvUser: TextView = itemView.findViewById(R.id.tv_user)
    }
}
