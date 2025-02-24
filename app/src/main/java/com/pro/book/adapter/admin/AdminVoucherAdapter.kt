package com.pro.book.adapter.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.admin.AdminVoucherAdapter.AdminVoucherViewHolder
import com.pro.book.listener.IOnAdminManagerVoucherListener
import com.pro.book.model.Voucher

class AdminVoucherAdapter(
    private val mListVoucher: List<Voucher>?,
    private val mListener: IOnAdminManagerVoucherListener
) : RecyclerView.Adapter<AdminVoucherViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminVoucherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_voucher, parent, false)
        return AdminVoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminVoucherViewHolder, position: Int) {
        val voucher = mListVoucher!![position]
        holder.tvTitle.text = voucher.title
        holder.tvMinimum.text = voucher.minimumText
        holder.imgEdit.setOnClickListener { mListener.onClickUpdateVoucher(voucher) }
        holder.imgDelete.setOnClickListener { mListener.onClickDeleteVoucher(voucher) }
    }

    override fun getItemCount(): Int {
        if (mListVoucher != null) {
            return mListVoucher.size
        }
        return 0
    }

    class AdminVoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvMinimum: TextView = itemView.findViewById(R.id.tv_minimum)
        val imgEdit: ImageView = itemView.findViewById(R.id.img_edit)
        val imgDelete: ImageView = itemView.findViewById(R.id.img_delete)
    }
}
