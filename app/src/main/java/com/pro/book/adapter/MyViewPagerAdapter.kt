package com.pro.book.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pro.book.fragment.AccountFragment
import com.pro.book.fragment.HistoryFragment
import com.pro.book.fragment.HomeFragment

class MyViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> HistoryFragment()

            2 -> AccountFragment()

            else -> HomeFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}
