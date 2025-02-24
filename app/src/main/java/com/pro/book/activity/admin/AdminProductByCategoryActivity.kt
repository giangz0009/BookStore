package com.pro.book.activity.admin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.BaseActivity
import com.pro.book.adapter.admin.AdminProductAdapter
import com.pro.book.listener.IOnAdminManagerProductListener
import com.pro.book.model.Category
import com.pro.book.model.Product
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.startActivity

class AdminProductByCategoryActivity : BaseActivity() {
    private var mListProduct: MutableList<Product>? = null
    private var mAdminProductAdapter: AdminProductAdapter? = null
    private var mCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_product_by_category)

        loadDataIntent()
        initView()
        loadListProduct()
    }

    private fun loadDataIntent() {
        val bundleReceived = intent.extras
        if (bundleReceived != null) {
            mCategory = bundleReceived[Constant.KEY_INTENT_CATEGORY_OBJECT] as Category?
        }
    }

    private fun initView() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        imgToolbarBack.setOnClickListener { onBackPressed() }
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        tvToolbarTitle.text = mCategory!!.name

        val rcvData = findViewById<RecyclerView>(R.id.rcv_data)
        val linearLayoutManager = LinearLayoutManager(this)
        rcvData.layoutManager = linearLayoutManager
        mListProduct = ArrayList()
        mAdminProductAdapter =
            AdminProductAdapter(mListProduct, object : IOnAdminManagerProductListener {
                override fun onClickUpdateProduct(product: Product) {
                    onClickEditProduct(product)
                }

                override fun onClickDeleteProduct(product: Product) {
                    deleteProductItem(product)
                }
            })
        rcvData.adapter = mAdminProductAdapter
    }

    private fun onClickEditProduct(product: Product) {
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_INTENT_PRODUCT_OBJECT, product)
        startActivity(this, AdminAddProductActivity::class.java, bundle)
    }

    private fun deleteProductItem(product: Product) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.msg_delete_title))
            .setMessage(getString(R.string.msg_confirm_delete))
            .setPositiveButton(getString(R.string.action_ok)) { _: DialogInterface?, _: Int ->
                get(
                    this
                ).productDatabaseReference
                    .child(product.id.toString())
                    .removeValue { _: DatabaseError?, _: DatabaseReference? ->
                        Toast.makeText(
                            this,
                            getString(R.string.msg_delete_product_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .setNegativeButton(getString(R.string.action_cancel), null)
            .show()
    }

    private fun resetListProduct() {
        if (mListProduct != null) {
            mListProduct!!.clear()
        } else {
            mListProduct = ArrayList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadListProduct() {
        get(this).productDatabaseReference
            .orderByChild("category_id").equalTo(mCategory!!.id.toDouble())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    resetListProduct()
                    for (dataSnapshot in snapshot.children) {
                        val product = dataSnapshot.getValue(
                            Product::class.java
                        )
                        if (product == null) return
                        mListProduct!!.add(0, product)
                    }
                    if (mAdminProductAdapter != null) mAdminProductAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}