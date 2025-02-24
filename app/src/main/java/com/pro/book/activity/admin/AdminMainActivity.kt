package com.pro.book.activity.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pro.book.R
import com.pro.book.activity.BaseActivity
import com.pro.book.adapter.admin.AdminViewPagerAdapter

class AdminMainActivity : BaseActivity() {
    private var viewPager2: ViewPager2? = null
    private var bottomNavigation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        initUi()
        setupActivity()
    }

    private fun initUi() {
        viewPager2 = findViewById(R.id.viewpager_2)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setupActivity() {
        viewPager2!!.isUserInputEnabled = false
        val adminViewPagerAdapter = AdminViewPagerAdapter(this)
        viewPager2!!.adapter = adminViewPagerAdapter

        viewPager2!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> bottomNavigation!!.menu.findItem(R.id.nav_category).setChecked(true)
                    1 -> bottomNavigation!!.menu.findItem(R.id.nav_product).setChecked(true)
                    2 -> bottomNavigation!!.menu.findItem(R.id.nav_order).setChecked(true)
                    3 -> bottomNavigation!!.menu.findItem(R.id.nav_settings).setChecked(true)
                }
            }
        })

        bottomNavigation!!.setOnItemSelectedListener { item: MenuItem ->
            val id = item.itemId
            when (id) {
                R.id.nav_category -> {
                    viewPager2!!.currentItem = 0
                }
                R.id.nav_product -> {
                    viewPager2!!.currentItem = 1
                }
                R.id.nav_order -> {
                    viewPager2!!.currentItem = 2
                }
                R.id.nav_settings -> {
                    viewPager2!!.currentItem = 3
                }
            }
            true
        }
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
            .onPositive { _: MaterialDialog?, _: DialogAction? -> finishAffinity() }
            .negativeText(getString(R.string.action_cancel))
            .cancelable(false)
            .show()
    }
}