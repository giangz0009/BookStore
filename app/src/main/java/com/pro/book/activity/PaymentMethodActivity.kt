package com.pro.book.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.PaymentMethodAdapter
import com.pro.book.adapter.PaymentMethodAdapter.IClickPaymentMethodListener
import com.pro.book.event.PaymentMethodSelectedEvent
import com.pro.book.model.PaymentMethod
import com.pro.book.utils.Constant
import org.greenrobot.eventbus.EventBus

class PaymentMethodActivity : BaseActivity() {
    private var listPaymentMethod: MutableList<PaymentMethod>? = null
    private var paymentMethodAdapter: PaymentMethodAdapter? = null
    private var paymentMethodSelectedId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        dataIntent
        initToolbar()
        initUi()
        listPaymentMethodFromFirebase
    }

    private val dataIntent: Unit
        get() {
            val bundle = intent.extras ?: return
            paymentMethodSelectedId = bundle.getInt(Constant.PAYMENT_METHOD_ID, 0)
        }

    private fun initUi() {
        val rcvPaymentMethod = findViewById<RecyclerView>(R.id.rcv_payment_method)
        val linearLayoutManager = LinearLayoutManager(this)
        rcvPaymentMethod.layoutManager = linearLayoutManager
        listPaymentMethod = ArrayList()
        paymentMethodAdapter = PaymentMethodAdapter(listPaymentMethod,
            object : IClickPaymentMethodListener {
                override fun onClickPaymentMethodItem(paymentMethod: PaymentMethod?) {
                    paymentMethod?.let {
                        handleClickPaymentMethod(
                            it
                        )
                    }
                }
            })
        rcvPaymentMethod.adapter = paymentMethodAdapter
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { onBackPressed() }
        tvToolbarTitle.text = getString(R.string.title_payment_method)
    }

    @get:SuppressLint("NotifyDataSetChanged")
    private val listPaymentMethodFromFirebase: Unit
        get() {
            resetListPaymentMethod()
            listPaymentMethod!!.add(
                PaymentMethod(
                    1,
                    getString(R.string.title_payment_method_cash),
                    getString(R.string.title_payment_method_cash_desc)
                )
            )
            listPaymentMethod!!.add(
                PaymentMethod(
                    2,
                    getString(R.string.title_payment_method_card),
                    getString(R.string.title_payment_method_card_desc)
                )
            )
            listPaymentMethod!!.add(
                PaymentMethod(
                    3,
                    getString(R.string.title_payment_method_bank),
                    getString(R.string.title_payment_method_bank_desc)
                )
            )
            listPaymentMethod!!.add(
                PaymentMethod(
                    4,
                    getString(R.string.title_payment_method_zalopay),
                    getString(R.string.title_payment_method_zalopay_desc)
                )
            )

            if (paymentMethodSelectedId > 0 && listPaymentMethod != null) {
                for (paymentMethod in listPaymentMethod!!) {
                    if (paymentMethod.id == paymentMethodSelectedId) {
                        paymentMethod.isSelected = true
                        break
                    }
                }
            }
            if (paymentMethodAdapter != null) paymentMethodAdapter!!.notifyDataSetChanged()
        }

    private fun resetListPaymentMethod() {
        if (listPaymentMethod != null) {
            listPaymentMethod!!.clear()
        } else {
            listPaymentMethod = ArrayList()
        }
    }

    private fun handleClickPaymentMethod(paymentMethod: PaymentMethod) {
        EventBus.getDefault().post(PaymentMethodSelectedEvent(paymentMethod))
        finish()
    }
}
