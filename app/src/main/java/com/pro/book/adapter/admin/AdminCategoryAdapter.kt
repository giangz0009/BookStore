package com.pro.book.adapter.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.admin.AdminCategoryAdapter.AdminCategoryViewHolder
import com.pro.book.listener.IOnAdminManagerCategoryListener
import com.pro.book.model.Category

class AdminCategoryAdapter(
    private val mListCategory: List<Category>?,
    private val mListener: IOnAdminManagerCategoryListener
) : RecyclerView.Adapter<AdminCategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_category, parent, false)
        return AdminCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminCategoryViewHolder, position: Int) {
        val category = mListCategory!![position]
        holder.tvName.text = category.name
        holder.imgEdit.setOnClickListener { mListener.onClickUpdateCategory(category) }
        holder.imgDelete.setOnClickListener { mListener.onClickDeleteCategory(category) }
        holder.layoutItem.setOnClickListener { mListener.onClickItemCategory(category) }
    }

    override fun getItemCount(): Int {
        if (mListCategory != null) {
            return mListCategory.size
        }
        return 0
    }

    class AdminCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val imgEdit: ImageView = itemView.findViewById(R.id.img_edit)
        val imgDelete: ImageView = itemView.findViewById(R.id.img_delete)
        val layoutItem: RelativeLayout = itemView.findViewById(R.id.layout_item)
    }
}
