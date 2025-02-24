package com.pro.book.activity.admin

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.BaseActivity
import com.pro.book.adapter.admin.AdminSelectAdapter
import com.pro.book.model.Category
import com.pro.book.model.Product
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.hideSoftKeyboard
import com.pro.book.utils.StringUtil.isEmpty

class AdminAddProductActivity : BaseActivity() {
    private var tvToolbarTitle: TextView? = null
    private var edtName: EditText? = null
    private var edtDescription: EditText? = null
    private var edtPrice: EditText? = null
    private var edtPromotion: EditText? = null
    private var edtImage: EditText? = null
    private var edtImageBanner: EditText? = null
    private var edtInfo: EditText? = null
    private var chbFeatured: CheckBox? = null
    private var spnCategory: Spinner? = null
    private var btnAddOrEdit: Button? = null

    private var isUpdate = false
    private var mProduct: Product? = null
    private var mCategorySelected: Category? = null
    private var mValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_product)

        loadDataIntent()
        initUi()
        initData()
    }

    private fun loadDataIntent() {
        val bundleReceived = intent.extras
        if (bundleReceived != null) {
            isUpdate = true
            mProduct = bundleReceived[Constant.KEY_INTENT_PRODUCT_OBJECT] as Product?
        }
    }

    private fun initUi() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title)
        edtName = findViewById(R.id.edt_name)
        edtDescription = findViewById(R.id.edt_description)
        edtInfo = findViewById(R.id.edt_info)
        edtPrice = findViewById(R.id.edt_price)
        edtPromotion = findViewById(R.id.edt_promotion)
        edtImage = findViewById(R.id.edt_image)
        edtImageBanner = findViewById(R.id.edt_image_banner)
        chbFeatured = findViewById(R.id.chb_featured)
        btnAddOrEdit = findViewById(R.id.btn_add_or_edit)
        spnCategory = findViewById(R.id.spn_category)
        imgToolbarBack.setOnClickListener { onBackPressed() }
        btnAddOrEdit?.setOnClickListener { addOrEditProduct() }
    }

    private fun initData() {
        if (isUpdate) {
            tvToolbarTitle!!.text = getString(R.string.label_update_product)
            btnAddOrEdit!!.text = getString(R.string.action_edit)

            edtName!!.setText(mProduct!!.name)
            edtDescription!!.setText(mProduct!!.description)
            edtInfo!!.setText(mProduct!!.info)
            edtPrice!!.setText(mProduct!!.price.toString())
            edtPromotion!!.setText(mProduct!!.sale.toString())
            edtImage!!.setText(mProduct!!.image)
            edtImageBanner!!.setText(mProduct!!.banner)
            chbFeatured!!.isChecked = mProduct!!.isFeatured
        } else {
            tvToolbarTitle!!.text = getString(R.string.label_add_product)
            btnAddOrEdit!!.text = getString(R.string.action_add)
        }
        loadListCategory()
    }

    private fun loadListCategory() {
        mValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<Category?> = ArrayList()
                for (dataSnapshot in snapshot.children) {
                    val category = dataSnapshot.getValue(
                        Category::class.java
                    )
                    if (category == null) return
                    list.add(0, category)
                }
                val adapter = AdminSelectAdapter(
                    this@AdminAddProductActivity,
                    R.layout.item_choose_option, list
                )
                spnCategory!!.adapter = adapter
                spnCategory!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        mCategorySelected = adapter.getItem(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                if (mProduct != null && mProduct!!.category_id > 0) {
                    spnCategory!!.setSelection(getPositionSelected(list, mProduct!!.category_id))
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        get(this).categoryDatabaseReference
            .addValueEventListener(mValueEventListener!!)
    }

    private fun getPositionSelected(list: List<Category?>, id: Long): Int {
        var position = 0
        for (i in list.indices) {
            if (id == list[i]!!.id) {
                position = i
                break
            }
        }
        return position
    }

    private fun addOrEditProduct() {
        val strName = edtName!!.text.toString().trim { it <= ' ' }
        val strDescription = edtDescription!!.text.toString().trim { it <= ' ' }
        val strInfo = edtInfo!!.text.toString().trim { it <= ' ' }
        val strPrice = edtPrice!!.text.toString().trim { it <= ' ' }
        var strPromotion = edtPromotion!!.text.toString().trim { it <= ' ' }
        val strImage = edtImage!!.text.toString().trim { it <= ' ' }
        val strImageBanner = edtImageBanner!!.text.toString().trim { it <= ' ' }

        if (isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_require), Toast.LENGTH_SHORT).show()
            return
        }

        if (isEmpty(strDescription)) {
            Toast.makeText(this, getString(R.string.msg_description_require), Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (isEmpty(strPrice)) {
            Toast.makeText(this, getString(R.string.msg_price_require), Toast.LENGTH_SHORT).show()
            return
        }

        if (isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_require), Toast.LENGTH_SHORT).show()
            return
        }

        if (isEmpty(strImageBanner)) {
            Toast.makeText(this, getString(R.string.msg_image_banner_require), Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (isEmpty(strInfo)) {
            Toast.makeText(this, getString(R.string.msg_product_info), Toast.LENGTH_SHORT).show()
            return
        }

        if (isEmpty(strPromotion)) {
            strPromotion = "0"
        }

        // Update product
        if (isUpdate) {
            showProgressDialog(true)
            val map: MutableMap<String, Any?> = HashMap()
            map["name"] = strName
            map["description"] = strDescription
            map["info"] = strInfo
            map["price"] = strPrice.toInt()
            map["sale"] = strPromotion.toInt()
            map["image"] = strImage
            map["banner"] = strImageBanner
            map["featured"] = chbFeatured!!.isChecked
            map["category_id"] = mCategorySelected!!.id
            map["category_name"] = mCategorySelected!!.name

            get(this).productDatabaseReference
                .child(mProduct!!.id.toString())
                .updateChildren(map) { _: DatabaseError?, _: DatabaseReference? ->
                    showProgressDialog(false)
                    Toast.makeText(
                        this,
                        getString(R.string.msg_edit_product_success), Toast.LENGTH_SHORT
                    ).show()
                    hideSoftKeyboard(this)
                }
            return
        }

        // Add product
        showProgressDialog(true)
        val productId = System.currentTimeMillis()
        val product = Product()
        product.id = productId
        product.name = strName
        product.description = strDescription
        product.info = strInfo
        product.price = strPrice.toInt()
        product.sale = strPromotion.toInt()
        product.image = strImage
        product.banner = strImageBanner
        product.isFeatured = chbFeatured!!.isChecked

        product.category_id = mCategorySelected!!.id
        product.category_name = mCategorySelected!!.name

        get(this).productDatabaseReference
            .child(productId.toString())
            .setValue(product) { _: DatabaseError?, _: DatabaseReference? ->
                showProgressDialog(false)
                edtName!!.setText("")
                edtDescription!!.setText("")
                edtInfo!!.setText("")
                edtPrice!!.setText("")
                edtPromotion!!.setText("0")
                edtImage!!.setText("")
                edtImageBanner!!.setText("")
                chbFeatured!!.isChecked = false
                spnCategory!!.setSelection(0)
                hideSoftKeyboard(this)
                Toast.makeText(
                    this,
                    getString(R.string.msg_add_product_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mValueEventListener != null) {
            get(this).categoryDatabaseReference
                .removeEventListener(mValueEventListener!!)
        }
    }
}