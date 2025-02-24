package com.pro.book.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pro.book.R
import com.pro.book.activity.MainActivity
import com.pro.book.adapter.OrderPagerAdapter
import com.pro.book.model.TabOrder
import java.util.Locale

class HistoryFragment : Fragment() {
    private var mView: View? = null
    private var viewPagerOrder: ViewPager2? = null
    private var tabOrder: TabLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_history, container, false)

        initToolbar()
        initUi()
        displayTabsOrder()

        return mView
    }

    private fun initToolbar() {
        val imgToolbarBack = mView!!.findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = mView!!.findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { backToHomeScreen() }
        tvToolbarTitle.text = getString(R.string.nav_history)
    }

    private fun backToHomeScreen() {
        val mainActivity = activity as MainActivity? ?: return
        mainActivity.viewPager2?.currentItem = 0
    }

    private fun initUi() {
        viewPagerOrder = mView!!.findViewById(R.id.view_pager_order)
        viewPagerOrder?.setUserInputEnabled(false)
        tabOrder = mView!!.findViewById(R.id.tab_order)
    }

    private fun displayTabsOrder() {
        val list: MutableList<TabOrder> = ArrayList()
        list.add(TabOrder(TabOrder.TAB_ORDER_PROCESS, getString(R.string.label_process)))
        list.add(TabOrder(TabOrder.TAB_ORDER_DONE, getString(R.string.label_done)))
        if (activity == null) return
        viewPagerOrder!!.offscreenPageLimit = list.size
        val adapter = OrderPagerAdapter(requireActivity(), list)
        viewPagerOrder!!.adapter = adapter
        TabLayoutMediator(
            tabOrder!!, viewPagerOrder!!
        ) { tab: TabLayout.Tab, position: Int -> tab.setText(list[position].name.lowercase(Locale.getDefault())) }
            .attach()
    }
}
