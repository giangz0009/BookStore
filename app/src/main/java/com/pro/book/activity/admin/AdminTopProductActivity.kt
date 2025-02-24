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
import com.pro.book.adapter.admin.AdminTopProductAdapter
import com.pro.book.listener.IGetDateListener
import com.pro.book.listener.IOnSingleClickListener
import com.pro.book.model.Order
import com.pro.book.model.ProductOrder
import com.pro.book.utils.DateTimeUtils.convertDate2ToTimeStamp
import com.pro.book.utils.DateTimeUtils.convertTimeStampToDate_2
import com.pro.book.utils.GlobalFunction.showDatePicker
import com.pro.book.utils.StringUtil.isEmpty
import java.util.Collections

class AdminTopProductActivity : BaseActivity() {
    private var tvDateFrom: TextView? = null
    private var tvDateTo: TextView? = null
    private var rcvData: RecyclerView? = null
    private var mListProductOrder: MutableList<ProductOrder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_top_product)

        initToolbar()
        initUi()
        initListener()
        listTopProduct
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        imgToolbarBack.setOnClickListener { onBackPressed() }
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        tvToolbarTitle.text = getString(R.string.label_top_product)
    }

    private fun initUi() {
        tvDateFrom = findViewById(R.id.tv_date_from)
        tvDateTo = findViewById(R.id.tv_date_to)
        rcvData = findViewById(R.id.rcv_data)
    }

    private fun initListener() {
        tvDateFrom!!.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showDatePicker(
                    this@AdminTopProductActivity,
                    tvDateFrom!!.text.toString(),
                    object : IGetDateListener {
                        override fun getDate(date: String?) {
                            tvDateFrom!!.text = date
                            listTopProduct
                        }
                    })
            }
        })

        tvDateTo!!.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showDatePicker(
                    this@AdminTopProductActivity,
                    tvDateTo!!.text.toString(),
                    object : IGetDateListener {
                        override fun getDate(date: String?) {
                            tvDateTo!!.text = date
                            listTopProduct
                        }
                    })
            }
        })
    }

    private val listTopProduct: Unit
        get() {
            get(this).orderDatabaseReference
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list: MutableList<Order?> = ArrayList()
                        for (dataSnapshot in snapshot.children) {
                            val order = dataSnapshot.getValue(
                                Order::class.java
                            )
                            if (canAddOrder(order)) {
                                list.add(0, order)
                            }
                        }
                        handleDataTopProduct(list)
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

    private fun handleDataTopProduct(list: List<Order?>?) {
        if (list == null) return
        if (mListProductOrder != null) {
            mListProductOrder!!.clear()
        } else {
            mListProductOrder = ArrayList()
        }
        for (order in list) {
            for (productOrder in order!!.products!!) {
                val productOrderId = productOrder.id
                if (checkProductOrderExist(productOrderId)) {
                    val productOrderExist = getProductOrderFromId(productOrderId)
                    productOrderExist!!.count = productOrderExist.count + productOrder.count
                } else {
                    mListProductOrder!!.add(productOrder)
                }
            }
        }
        val listProductOrderDisplay: List<ProductOrder> = ArrayList(mListProductOrder!!)
        Collections.sort(listProductOrderDisplay) { productOrder1: ProductOrder, productOrder2: ProductOrder -> productOrder2.count - productOrder1.count }
        displayDataTopProduct(listProductOrderDisplay)
    }

    private fun checkProductOrderExist(productOrderId: Long): Boolean {
        if (mListProductOrder == null || mListProductOrder!!.isEmpty()) return false
        var result = false
        for (productOrder in mListProductOrder!!) {
            if (productOrderId == productOrder.id) {
                result = true
                break
            }
        }
        return result
    }

    private fun getProductOrderFromId(productOrderId: Long): ProductOrder? {
        var result: ProductOrder? = null
        for (productOrder in mListProductOrder!!) {
            if (productOrderId == productOrder.id) {
                result = productOrder
                break
            }
        }
        return result
    }

    private fun displayDataTopProduct(list: List<ProductOrder>?) {
        if (list == null) return
        val linearLayoutManager = LinearLayoutManager(this)
        rcvData!!.layoutManager = linearLayoutManager
        val adminTopProductAdapter = AdminTopProductAdapter(list)
        rcvData!!.adapter = adminTopProductAdapter
    }
}