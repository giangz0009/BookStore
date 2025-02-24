package com.pro.book.activity.admin

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.BaseActivity
import com.pro.book.adapter.admin.AdminRevenueAdapter
import com.pro.book.listener.IGetDateListener
import com.pro.book.listener.IOnSingleClickListener
import com.pro.book.model.Order
import com.pro.book.utils.Constant
import com.pro.book.utils.DateTimeUtils.convertDate2ToTimeStamp
import com.pro.book.utils.DateTimeUtils.convertTimeStampToDate_2
import com.pro.book.utils.GlobalFunction.showDatePicker
import com.pro.book.utils.StringUtil.isEmpty

class AdminRevenueActivity : BaseActivity() {
    private var tvDateFrom: TextView? = null
    private var tvDateTo: TextView? = null
    private var tvTotalValue: TextView? = null
    private var rcvOrderHistory: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_revenue)

        initToolbar()
        initUi()
        initListener()
        listRevenue
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        imgToolbarBack.setOnClickListener { onBackPressed() }
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        tvToolbarTitle.text = getString(R.string.revenue)
    }

    private fun initUi() {
        tvDateFrom = findViewById(R.id.tv_date_from)
        tvDateTo = findViewById(R.id.tv_date_to)
        tvTotalValue = findViewById(R.id.tv_total_value)
        rcvOrderHistory = findViewById(R.id.rcv_order_history)
    }

    private fun initListener() {
        tvDateFrom!!.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showDatePicker(
                    this@AdminRevenueActivity,
                    tvDateFrom!!.text.toString(),
                    object : IGetDateListener {
                        override fun getDate(date: String?) {
                            tvDateFrom!!.text = date
                            listRevenue
                        }
                    })
            }
        })

        tvDateTo!!.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showDatePicker(
                    this@AdminRevenueActivity,
                    tvDateTo!!.text.toString(),
                    object : IGetDateListener {
                        override fun getDate(date: String?) {
                            tvDateTo!!.text = date
                            listRevenue
                        }
                    })
            }
        })
    }

    private val listRevenue: Unit
        get() {
            get(this).orderDatabaseReference
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list: MutableList<Order> = ArrayList()
                        for (dataSnapshot in snapshot.children) {
                            val order = dataSnapshot.getValue(
                                Order::class.java
                            )
                            if (canAddOrder(order)) {
                                order?.let { list.add(0, it) }
                            }
                        }
                        handleDataHistories(list)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

    private fun canAddOrder(order: Order?): Boolean {
        if (order == null) return false
        if (Order.STATUS_COMPLETE != order.status) return false
        val strDateFrom = tvDateFrom!!.text.toString()
        val strDateTo = tvDateTo!!.text.toString()
        if (isEmpty(strDateFrom) && isEmpty(strDateTo)) {
            return true
        }
        val strDateOrder = convertTimeStampToDate_2(order.id)
        val longOrder = convertDate2ToTimeStamp(strDateOrder).toLong()

        if (isEmpty(strDateFrom) && !isEmpty(strDateTo)) {
            val longDateTo = convertDate2ToTimeStamp(strDateTo).toLong()
            return longOrder <= longDateTo
        }
        if (!isEmpty(strDateFrom) && isEmpty(strDateTo)) {
            val longDateFrom = convertDate2ToTimeStamp(strDateFrom).toLong()
            return longOrder >= longDateFrom
        }
        val longDateTo = convertDate2ToTimeStamp(strDateTo).toLong()
        val longDateFrom = convertDate2ToTimeStamp(strDateFrom).toLong()
        return longOrder in longDateFrom..longDateTo
    }

    private fun handleDataHistories(list: List<Order>?) {
        if (list == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(this)
        rcvOrderHistory!!.layoutManager = linearLayoutManager
        val adminRevenueAdapter = AdminRevenueAdapter(list)
        rcvOrderHistory!!.adapter = adminRevenueAdapter

        // Calculate total
        val strTotalValue = getTotalValues(list).toString() + Constant.CURRENCY
        tvTotalValue!!.text = strTotalValue
    }

    private fun getTotalValues(list: List<Order?>?): Int {
        if (list.isNullOrEmpty()) {
            return 0
        }

        var total = 0
        for (order in list) {
            total += order!!.total
        }
        return total
    }
}