package com.pro.book.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.database.ProductDatabase.Companion.getInstance
import com.pro.book.event.DisplayCartEvent
import com.pro.book.model.Product
import com.pro.book.model.RatingReview
import com.pro.book.utils.Constant
import com.pro.book.utils.GlideUtils.loadUrl
import com.pro.book.utils.GlobalFunction.startActivity
import org.greenrobot.eventbus.EventBus

class ProductDetailActivity : BaseActivity() {
    private var imgProduct: ImageView? = null
    private var tvName: TextView? = null
    private var tvPriceSale: TextView? = null
    private var tvDescription: TextView? = null
    private var tvSub: TextView? = null
    private var tvAdd: TextView? = null
    private var tvCount: TextView? = null
    private var layoutRatingAndReview: RelativeLayout? = null
    private var tvRate: TextView? = null
    private var tvCountReview: TextView? = null
    private var tvInfo: TextView? = null
    private var tvTotal: TextView? = null
    private var tvAddOrder: TextView? = null

    private var mProductId: Long = 0
    private var mProductOld: Product? = null
    private var mProduct: Product? = null

    private var mProductValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        dataIntent
        initUi()
        productDetailFromFirebase
    }

    private val dataIntent: Unit
        get() {
            val bundle = intent.extras ?: return
            mProductId = bundle.getLong(Constant.PRODUCT_ID)
            if (bundle[Constant.PRODUCT_OBJECT] != null) {
                mProductOld = bundle[Constant.PRODUCT_OBJECT] as Product?
            }
        }

    private fun initUi() {
        imgProduct = findViewById(R.id.img_product)
        tvName = findViewById(R.id.tv_name)
        tvPriceSale = findViewById(R.id.tv_price_sale)
        tvDescription = findViewById(R.id.tv_description)
        tvSub = findViewById(R.id.tv_sub)
        tvAdd = findViewById(R.id.tv_add)
        tvCount = findViewById(R.id.tv_count)
        layoutRatingAndReview = findViewById(R.id.layout_rating_and_review)
        tvCountReview = findViewById(R.id.tv_count_review)
        tvRate = findViewById(R.id.tv_rate)
        tvInfo = findViewById(R.id.tv_info)
        tvTotal = findViewById(R.id.tv_total)
        tvAddOrder = findViewById(R.id.tv_add_order)
    }

    private val productDetailFromFirebase: Unit
        get() {
            showProgressDialog(true)
            mProductValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    showProgressDialog(false)
                    mProduct = snapshot.getValue(Product::class.java)
                    if (mProduct == null) return

                    initToolbar()
                    initData()
                    initListener()
                }

                override fun onCancelled(error: DatabaseError) {
                    showProgressDialog(false)
                    showToastMessage(getString(R.string.msg_get_date_error))
                }
            }
            get(this).getProductDetailDatabaseReference(mProductId)
                .addValueEventListener(mProductValueEventListener!!)
        }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { finish() }
        tvToolbarTitle.text = mProduct!!.name
    }

    private fun initData() {
        if (mProduct == null) return
        loadUrl(mProduct!!.image, imgProduct!!)
        tvName!!.text = mProduct!!.name
        val strPrice = mProduct!!.realPrice.toString() + Constant.CURRENCY
        tvPriceSale!!.text = strPrice
        tvDescription!!.text = mProduct!!.description
        if (mProductOld != null) {
            mProduct!!.count = mProductOld!!.count
        } else {
            mProduct!!.count = 1
        }
        tvCount!!.text = mProduct!!.count.toString()
        tvRate!!.text = mProduct!!.rate.toString()
        val strCountReview = "(" + mProduct!!.countReviews + ")"
        tvCountReview!!.text = strCountReview

        if (mProduct!!.info != null) {
            tvInfo!!.text = mProduct!!.info
        }

        calculatorTotalPrice()
    }

    private fun initListener() {
        tvSub!!.setOnClickListener {
            val count = tvCount!!.text.toString().toInt()
            if (count <= 1) {
                return@setOnClickListener
            }
            val newCount = tvCount!!.text.toString().toInt() - 1
            tvCount!!.text = newCount.toString()
            calculatorTotalPrice()
        }

        tvAdd!!.setOnClickListener {
            val newCount = tvCount!!.text.toString().toInt() + 1
            tvCount!!.text = newCount.toString()
            calculatorTotalPrice()
        }

        layoutRatingAndReview!!.setOnClickListener {
            val bundle = Bundle()
            val ratingReview = RatingReview(
                RatingReview.TYPE_RATING_REVIEW_PRODUCT,
                mProduct!!.id.toString()
            )
            bundle.putSerializable(Constant.RATING_REVIEW_OBJECT, ratingReview)
            startActivity(
                this@ProductDetailActivity,
                RatingReviewActivity::class.java, bundle
            )
        }

        tvAddOrder!!.setOnClickListener {
            if (!isProductInCart) {
                getInstance(this@ProductDetailActivity)!!.productDAO()!!
                    .insertProduct(mProduct!!)
            } else {
                getInstance(this@ProductDetailActivity)!!.productDAO()!!
                    .updateProduct(mProduct!!)
            }
//            startActivity(this@ProductDetailActivity, CartActivity::class.java)
            EventBus.getDefault().post(DisplayCartEvent())
            finish()
        }
    }

    private fun calculatorTotalPrice() {
        val count = tvCount!!.text.toString().trim { it <= ' ' }.toInt()
        val priceOneProduct = mProduct!!.realPrice
        val totalPrice = priceOneProduct * count
        val strTotalPrice = totalPrice.toString() + Constant.CURRENCY
        tvTotal!!.text = strTotalPrice

        mProduct!!.count = count
        mProduct!!.priceOneProduct = priceOneProduct
        mProduct!!.totalPrice = totalPrice
    }

    private val isProductInCart: Boolean
        get() {
            val list = getInstance(this)?.productDAO()!!.checkProductInCart(mProduct!!.id)
            return !list.isNullOrEmpty()
        }

    override fun onDestroy() {
        super.onDestroy()
        if (mProductValueEventListener != null) {
            get(this).getProductDetailDatabaseReference(mProductId)
                .removeEventListener(mProductValueEventListener!!)
        }
    }
}
