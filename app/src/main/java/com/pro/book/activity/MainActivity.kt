package com.pro.book.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pro.book.R
import com.pro.book.adapter.MyViewPagerAdapter
import com.pro.book.database.ProductDatabase.Companion.getInstance
import com.pro.book.event.DisplayCartEvent
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.startActivity
import com.pro.book.utils.StringUtil.isEmpty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseActivity() {
    private var mBottomNavigationView: BottomNavigationView? = null
    var viewPager2: ViewPager2? = null
        private set
    private var layoutCartBottom: RelativeLayout? = null
    private var tvCountItem: TextView? = null
    private var tvProductsName: TextView? = null
    private var tvAmount: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        initUi()

        mBottomNavigationView = findViewById(R.id.bottom_navigation)
        viewPager2 = findViewById(R.id.viewpager_2)
        viewPager2?.setUserInputEnabled(false)
        val myViewPagerAdapter = MyViewPagerAdapter(this)
        viewPager2?.setAdapter(myViewPagerAdapter)

        viewPager2?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> mBottomNavigationView?.menu?.findItem(R.id.nav_home)?.setChecked(true)
                    1 -> mBottomNavigationView?.menu?.findItem(R.id.nav_history)?.setChecked(true)
                    2 -> mBottomNavigationView?.menu?.findItem(R.id.nav_account)?.setChecked(true)
                }
            }
        })

        mBottomNavigationView?.setOnItemSelectedListener { item: MenuItem ->
            val id = item.itemId
            when (id) {
                R.id.nav_home -> {
                    viewPager2?.currentItem = 0
                }
                R.id.nav_history -> {
                    viewPager2?.currentItem = 1
                }
                R.id.nav_account -> {
                    viewPager2?.currentItem = 2
                }
            }
            true
        }

        displayLayoutCartBottom()
    }

    private fun initUi() {
        layoutCartBottom = findViewById(R.id.layout_cart_bottom)
        tvCountItem = findViewById(R.id.tv_count_item)
        tvProductsName = findViewById(R.id.tv_products_name)
        tvAmount = findViewById(R.id.tv_amount)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        showConfirmExitApp()
    }

    private fun showConfirmExitApp() {
        MaterialDialog.Builder(this)
            .title(getString(R.string.app_name))
            .content(getString(R.string.msg_exit_app))
            .positiveText(getString(R.string.action_ok))
            .onPositive { _: MaterialDialog?, _: DialogAction? -> finish() }
            .negativeText(getString(R.string.action_cancel))
            .cancelable(false)
            .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDisplayCartEvent(event: DisplayCartEvent?) {
        displayLayoutCartBottom()
    }

    private fun displayLayoutCartBottom() {
        val listProduct = getInstance(this)!!
            .productDAO()!!.listProductCart
        if (listProduct.isNullOrEmpty()) {
            layoutCartBottom!!.visibility = View.GONE
        } else {
            layoutCartBottom!!.visibility = View.VISIBLE
            val strCountItem = listProduct.size.toString() + " " + getString(R.string.label_item)
            tvCountItem!!.text = strCountItem

            var strProductsName = ""
            for (product in listProduct) {
                strProductsName += if (isEmpty(strProductsName)) {
                    product.name
                } else {
                    ", " + product.name
                }
            }
            if (isEmpty(strProductsName)) {
                tvProductsName!!.visibility = View.GONE
            } else {
                tvProductsName!!.visibility = View.VISIBLE
                tvProductsName!!.text = strProductsName
            }

            var amount = 0
            for (product in listProduct) {
                amount += product.totalPrice
            }
            val strAmount = amount.toString() + Constant.CURRENCY
            tvAmount!!.text = strAmount
        }
        layoutCartBottom!!.setOnClickListener {
            startActivity(
                this, CartActivity::class.java
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}
