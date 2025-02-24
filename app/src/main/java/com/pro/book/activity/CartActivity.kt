package com.pro.book.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pro.book.R
import com.pro.book.adapter.CartAdapter
import com.pro.book.adapter.CartAdapter.IClickCartListener
import com.pro.book.database.ProductDatabase.Companion.getInstance
import com.pro.book.event.AddressSelectedEvent
import com.pro.book.event.DisplayCartEvent
import com.pro.book.event.OrderSuccessEvent
import com.pro.book.event.PaymentMethodSelectedEvent
import com.pro.book.event.VoucherSelectedEvent
import com.pro.book.model.Address
import com.pro.book.model.Order
import com.pro.book.model.PaymentMethod
import com.pro.book.model.Product
import com.pro.book.model.ProductOrder
import com.pro.book.model.Voucher
import com.pro.book.prefs.DataStoreManager
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.startActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartActivity : BaseActivity() {
    private var rcvCart: RecyclerView? = null
    private var layoutAddOrder: LinearLayout? = null
    private var layoutPaymentMethod: RelativeLayout? = null
    private var tvPaymentMethod: TextView? = null

    private var layoutAddress: RelativeLayout? = null
    private var tvAddress: TextView? = null
    private var layoutVoucher: RelativeLayout? = null
    private var tvVoucher: TextView? = null
    private var tvNameVoucher: TextView? = null
    private var tvPriceProduct: TextView? = null
    private var tvCountItem: TextView? = null
    private var tvAmount: TextView? = null
    private var tvPriceVoucher: TextView? = null
    private var tvCheckout: TextView? = null

    private var listProductCart: MutableList<Product>? = null
    private var cartAdapter: CartAdapter? = null
    private var priceProduct = 0
    private var mAmount = 0
    private var paymentMethodSelected: PaymentMethod? = null
    private var addressSelected: Address? = null
    private var voucherSelected: Voucher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        initToolbar()
        initUi()
        initListener()
        initData()
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { finish() }
        tvToolbarTitle.text = getString(R.string.label_cart)
    }

    private fun initUi() {
        rcvCart = findViewById(R.id.rcv_cart)
        val linearLayoutManager = LinearLayoutManager(this)
        rcvCart?.setLayoutManager(linearLayoutManager)
        layoutAddOrder = findViewById(R.id.layout_add_order)
        layoutPaymentMethod = findViewById(R.id.layout_payment_method)
        tvPaymentMethod = findViewById(R.id.tv_payment_method)
        layoutAddress = findViewById(R.id.layout_address)
        tvAddress = findViewById(R.id.tv_address)
        layoutVoucher = findViewById(R.id.layout_voucher)
        tvVoucher = findViewById(R.id.tv_voucher)
        tvNameVoucher = findViewById(R.id.tv_name_voucher)
        tvCountItem = findViewById(R.id.tv_count_item)
        tvPriceProduct = findViewById(R.id.tv_price_product)
        tvAmount = findViewById(R.id.tv_amount)
        tvPriceVoucher = findViewById(R.id.tv_price_voucher)
        tvCheckout = findViewById(R.id.tv_checkout)
    }

    private fun initListener() {
        layoutAddOrder!!.setOnClickListener { finish() }
        layoutPaymentMethod!!.setOnClickListener {
            val bundle = Bundle()
            if (paymentMethodSelected != null) {
                bundle.putInt(Constant.PAYMENT_METHOD_ID, paymentMethodSelected!!.id)
            }
            startActivity(this@CartActivity, PaymentMethodActivity::class.java, bundle)
        }

        layoutAddress!!.setOnClickListener {
            val bundle = Bundle()
            if (addressSelected != null) {
                bundle.putLong(Constant.ADDRESS_ID, addressSelected!!.id)
            }
            startActivity(this@CartActivity, AddressActivity::class.java, bundle)
        }

        layoutVoucher!!.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constant.AMOUNT_VALUE, priceProduct)
            if (voucherSelected != null) {
                bundle.putLong(Constant.VOUCHER_ID, voucherSelected!!.id)
            }
            startActivity(this@CartActivity, VoucherActivity::class.java, bundle)
        }

        tvCheckout!!.setOnClickListener {
            if (listProductCart == null || listProductCart!!.isEmpty()) return@setOnClickListener
            if (paymentMethodSelected == null) {
                showToastMessage(getString(R.string.label_choose_payment_method))
                return@setOnClickListener
            }
            if (addressSelected == null) {
                showToastMessage(getString(R.string.label_choose_address))
                return@setOnClickListener
            }
            val orderBooking = Order()
            orderBooking.id = System.currentTimeMillis()
            orderBooking.userEmail = DataStoreManager.user?.email
            orderBooking.dateTime = System.currentTimeMillis().toString()
            val products: MutableList<ProductOrder> = ArrayList()
            for (product in listProductCart!!) {
                products.add(
                    ProductOrder(
                        product.id, product.name,
                        product.description, product.count,
                        product.priceOneProduct, product.image
                    )
                )
            }
            orderBooking.products = products
            orderBooking.price = priceProduct
            if (voucherSelected != null) {
                orderBooking.voucher = voucherSelected!!.getPriceDiscount(priceProduct)
            }
            orderBooking.total = mAmount
            orderBooking.paymentMethod = paymentMethodSelected!!.name
            orderBooking.address = addressSelected
            orderBooking.status = Order.STATUS_NEW

            val bundle = Bundle()
            bundle.putSerializable(Constant.ORDER_OBJECT, orderBooking)
            startActivity(this@CartActivity, PaymentActivity::class.java, bundle)
        }
    }

    private fun initData() {
        listProductCart = ArrayList()
        listProductCart = getInstance(this)!!.productDAO()!!.listProductCart
        if (listProductCart == null || listProductCart!!.isEmpty()) {
            return
        }
        cartAdapter = CartAdapter(listProductCart, object : IClickCartListener {
            override fun onClickDeleteItem(product: Product, position: Int) {
                getInstance(this@CartActivity)!!.productDAO()!!.deleteProduct(product)
                listProductCart!!.removeAt(position)
                cartAdapter!!.notifyItemRemoved(position)

                displayCountItemCart()
                calculateTotalPrice()
                EventBus.getDefault().post(DisplayCartEvent())
            }

            override fun onClickUpdateItem(product: Product, position: Int) {
                getInstance(this@CartActivity)!!.productDAO()!!.updateProduct(product)
                cartAdapter!!.notifyItemChanged(position)

                calculateTotalPrice()
                EventBus.getDefault().post(DisplayCartEvent())
            }

            override fun onClickEditItem(product: Product) {
                val bundle = Bundle()
                bundle.putLong(Constant.PRODUCT_ID, product.id)
                bundle.putSerializable(Constant.PRODUCT_OBJECT, product)
                startActivity(this@CartActivity, ProductDetailActivity::class.java, bundle)
            }
        })
        rcvCart!!.adapter = cartAdapter
        calculateTotalPrice()
        displayCountItemCart()
    }

    private fun displayCountItemCart() {
        val strCountItem = "(" + listProductCart!!.size + " " + getString(R.string.label_item) + ")"
        tvCountItem!!.text = strCountItem
    }

    private fun calculateTotalPrice() {
        if (listProductCart == null || listProductCart!!.isEmpty()) {
            val strZero = 0.toString() + Constant.CURRENCY
            priceProduct = 0
            tvPriceProduct!!.text = strZero

            mAmount = 0
            tvAmount!!.text = strZero
            return
        }

        var totalPrice = 0
        for (product in listProductCart!!) {
            totalPrice += product.totalPrice
        }

        priceProduct = totalPrice
        val strPriceProduct = priceProduct.toString() + Constant.CURRENCY
        tvPriceProduct!!.text = strPriceProduct

        mAmount = totalPrice
        if (voucherSelected != null) {
            val strPriceVoucher = ("-" + voucherSelected!!.getPriceDiscount(priceProduct)
                    + Constant.CURRENCY)
            tvPriceVoucher!!.text = strPriceVoucher

            mAmount -= voucherSelected!!.getPriceDiscount(priceProduct)
        }
        val strAmount = mAmount.toString() + Constant.CURRENCY
        tvAmount!!.text = strAmount
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPaymentMethodSelectedEvent(event: PaymentMethodSelectedEvent) {
        paymentMethodSelected = event.paymentMethod
        tvPaymentMethod!!.text = paymentMethodSelected!!.name
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddressSelectedEvent(event: AddressSelectedEvent) {
        addressSelected = event.address
        tvAddress!!.text = addressSelected!!.address
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVoucherSelectedEvent(event: VoucherSelectedEvent) {
        voucherSelected = event.voucher
        tvVoucher!!.text = voucherSelected!!.title
        tvNameVoucher!!.text = voucherSelected!!.title
        val strPriceVoucher = ("-" + voucherSelected!!.getPriceDiscount(priceProduct)
                + Constant.CURRENCY)
        tvPriceVoucher!!.text = strPriceVoucher
        calculateTotalPrice()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderSuccessEvent(event: OrderSuccessEvent?) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}