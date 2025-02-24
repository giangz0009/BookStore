package com.pro.book.activity.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.BaseActivity
import com.pro.book.model.Category
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.hideSoftKeyboard
import com.pro.book.utils.StringUtil.isEmpty

class AdminAddCategoryActivity : BaseActivity() {
    private var tvToolbarTitle: TextView? = null
    private var edtName: EditText? = null
    private var btnAddOrEdit: Button? = null

    private var isUpdate = false
    private var mCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_category)

        loadDataIntent()
        initUi()
        initView()
    }

    private fun loadDataIntent() {
        val bundleReceived = intent.extras
        if (bundleReceived != null) {
            isUpdate = true
            mCategory = bundleReceived[Constant.KEY_INTENT_CATEGORY_OBJECT] as Category?
        }
    }

    private fun initUi() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title)
        edtName = findViewById(R.id.edt_name)
        btnAddOrEdit = findViewById(R.id.btn_add_or_edit)

        imgToolbarBack.setOnClickListener { onBackPressed() }
        btnAddOrEdit?.setOnClickListener { addOrEditCategory() }
    }

    private fun initView() {
        if (isUpdate) {
            tvToolbarTitle!!.text = getString(R.string.label_update_category)
            btnAddOrEdit!!.text = getString(R.string.action_edit)

            edtName!!.setText(mCategory!!.name)
        } else {
            tvToolbarTitle!!.text = getString(R.string.label_add_category)
            btnAddOrEdit!!.text = getString(R.string.action_add)
        }
    }

    private fun addOrEditCategory() {
        val strName = edtName!!.text.toString().trim { it <= ' ' }

        if (isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_require), Toast.LENGTH_SHORT).show()
            return
        }

        // Update category
        if (isUpdate) {
            showProgressDialog(true)
            val map: MutableMap<String, Any> = HashMap()
            map["name"] = strName

            get(this).categoryDatabaseReference
                .child(mCategory!!.id.toString())
                .updateChildren(map) { _: DatabaseError?, _: DatabaseReference? ->
                    showProgressDialog(false)
                    Toast.makeText(
                        this,
                        getString(R.string.msg_edit_category_success), Toast.LENGTH_SHORT
                    ).show()
                    hideSoftKeyboard(this)
                }
            return
        }

        // Add category
        showProgressDialog(true)
        val categoryId = System.currentTimeMillis()
        val category = Category(categoryId, strName)
        get(this).categoryDatabaseReference
            .child(categoryId.toString())
            .setValue(category) { _: DatabaseError?, _: DatabaseReference? ->
                showProgressDialog(false)
                edtName!!.setText("")
                hideSoftKeyboard(this)
                Toast.makeText(
                    this,
                    getString(R.string.msg_add_category_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}