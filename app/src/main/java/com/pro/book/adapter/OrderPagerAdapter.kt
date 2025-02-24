package com.pro.book.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pro.book.fragment.OrderFragment
import com.pro.book.model.TabOrder

class OrderPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val listTabOrder: List<TabOrder>?
) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return OrderFragment.newInstance(listTabOrder!![position].type)
    }

    override fun getItemCount(): Int {
        if (listTabOrder != null) return listTabOrder.size
        return 0
    }
}
