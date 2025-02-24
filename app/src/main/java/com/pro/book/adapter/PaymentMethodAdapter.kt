package com.pro.book.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.PaymentMethodAdapter.PaymentMethodViewHolder
import com.pro.book.model.PaymentMethod
import com.pro.book.utils.Constant

class PaymentMethodAdapter(
    private val listPaymentMethod: List<PaymentMethod>?,
    private val iClickPaymentMethodListener: IClickPaymentMethodListener
) : RecyclerView.Adapter<PaymentMethodViewHolder>() {
    interface IClickPaymentMethodListener {
        fun onClickPaymentMethodItem(paymentMethod: PaymentMethod?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_method, parent, false)
        return PaymentMethodViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        val paymentMethod = listPaymentMethod!![position]

        when (paymentMethod.id) {
            Constant.TYPE_GOPAY -> holder.imgPaymentMethod.setImageResource(R.drawable.ic_gopay)
            Constant.TYPE_CREDIT -> holder.imgPaymentMethod.setImageResource(R.drawable.ic_credit)
            Constant.TYPE_BANK -> holder.imgPaymentMethod.setImageResource(R.drawable.ic_bank)
            Constant.TYPE_ZALO_PAY -> holder.imgPaymentMethod.setImageResource(R.drawable.ic_zalopay)
        }
        holder.tvName.text = paymentMethod.name
        holder.tvDescription.text = paymentMethod.description
        if (paymentMethod.isSelected) {
            holder.imgStatus.setImageResource(R.drawable.ic_item_selected)
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_item_unselect)
        }

        holder.layoutItem.setOnClickListener {
            iClickPaymentMethodListener.onClickPaymentMethodItem(
                paymentMethod
            )
        }
    }

    override fun getItemCount(): Int {
        if (listPaymentMethod != null) {
            return listPaymentMethod.size
        }
        return 0
    }

    class PaymentMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPaymentMethod: ImageView = itemView.findViewById(R.id.img_payment_method)
        val imgStatus: ImageView = itemView.findViewById(R.id.img_status)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val layoutItem: LinearLayout = itemView.findViewById(R.id.layout_item)
    }
}
