package com.pro.book.adapter.admin

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.pro.book.R
import com.pro.book.model.Category

class AdminSelectAdapter(
    private val context: Context, @LayoutRes resource: Int,
    list: List<Category?>
) : ArrayAdapter<Category?>(
    context, resource, list
) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_choose_option, null)
            val tvSelected = convertView.findViewById<TextView>(R.id.tv_selected)
            tvSelected.text = getItem(position)!!.name
        }
        return convertView!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = View.inflate(context, R.layout.item_drop_down_option, null)
        val tvName = view.findViewById<TextView>(R.id.textview_name)
        tvName.text = getItem(position)!!.name
        return view
    }
}
