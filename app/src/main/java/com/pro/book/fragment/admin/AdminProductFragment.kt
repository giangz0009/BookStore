package com.pro.book.fragment.admin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.admin.AdminAddProductActivity
import com.pro.book.adapter.admin.AdminProductAdapter
import com.pro.book.listener.IOnAdminManagerProductListener
import com.pro.book.model.Product
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.getTextSearch
import com.pro.book.utils.GlobalFunction.hideSoftKeyboard
import com.pro.book.utils.GlobalFunction.startActivity
import com.pro.book.utils.StringUtil.isEmpty
import java.util.Locale

class AdminProductFragment : Fragment() {
    private var mView: View? = null
    private var mListProduct: MutableList<Product>? = null
    private var mAdminProductAdapter: AdminProductAdapter? = null
    private var mChildEventListener: ChildEventListener? = null
    private var edtSearchName: EditText? = null
    private var imgSearch: ImageView? = null
    private var btnAdd: FloatingActionButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_admin_product, container, false)

        initUi()
        initView()
        initListener()
        loadListProduct("")

        return mView
    }

    private fun initUi() {
        edtSearchName = mView!!.findViewById(R.id.edt_search_name)
        imgSearch = mView!!.findViewById(R.id.img_search)
        btnAdd = mView!!.findViewById(R.id.btn_add)
    }

    private fun initView() {
        val rcvData = mView!!.findViewById<RecyclerView>(R.id.rcv_data)
        val linearLayoutManager = LinearLayoutManager(activity)
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
        rcvData.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    btnAdd!!.hide()
                } else {
                    btnAdd!!.show()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun initListener() {
        btnAdd!!.setOnClickListener { onClickAddProduct() }

        imgSearch!!.setOnClickListener { searchProduct() }

        edtSearchName!!.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchProduct()
                return@setOnEditorActionListener true
            }
            false
        }

        edtSearchName!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val strKey = s.toString().trim { it <= ' ' }
                if (strKey.isEmpty()) {
                    searchProduct()
                }
            }
        })
    }

    private fun onClickAddProduct() {
        startActivity(requireActivity(), AdminAddProductActivity::class.java)
    }

    private fun onClickEditProduct(product: Product) {
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_INTENT_PRODUCT_OBJECT, product)
        startActivity(requireActivity(), AdminAddProductActivity::class.java, bundle)
    }

    private fun deleteProductItem(product: Product) {
        AlertDialog.Builder(activity)
            .setTitle(getString(R.string.msg_delete_title))
            .setMessage(getString(R.string.msg_confirm_delete))
            .setPositiveButton(getString(R.string.action_ok)) { _: DialogInterface?, _: Int ->
                if (activity == null) {
                    return@setPositiveButton
                }
                get(requireActivity()).productDatabaseReference
                    .child(product.id.toString())
                    .removeValue { _: DatabaseError?, _: DatabaseReference? ->
                        Toast.makeText(
                            activity,
                            getString(R.string.msg_delete_product_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .setNegativeButton(getString(R.string.action_cancel), null)
            .show()
    }

    private fun searchProduct() {
        val strKey = edtSearchName!!.text.toString().trim { it <= ' ' }
        resetListProduct()
        if (activity != null && mChildEventListener != null) {
            get(requireActivity()).productDatabaseReference
                .removeEventListener(mChildEventListener!!)
        }
        loadListProduct(strKey)
        hideSoftKeyboard(requireActivity())
    }

    private fun resetListProduct() {
        if (mListProduct != null) {
            mListProduct!!.clear()
        } else {
            mListProduct = ArrayList()
        }
    }

    private fun loadListProduct(keyword: String?) {
        if (activity == null) return
        mChildEventListener = object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val product = dataSnapshot.getValue(
                    Product::class.java
                )
                if (product == null || mListProduct == null) return
                if (isEmpty(keyword)) {
                    mListProduct!!.add(0, product)
                } else {
                    if (getTextSearch(product.name).lowercase(Locale.getDefault())
                            .trim { it <= ' ' }
                            .contains(
                                getTextSearch(keyword).lowercase(Locale.getDefault())
                                    .trim { it <= ' ' })
                    ) {
                        mListProduct!!.add(0, product)
                    }
                }
                if (mAdminProductAdapter != null) mAdminProductAdapter!!.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val product = dataSnapshot.getValue(
                    Product::class.java
                )
                if (product == null || mListProduct == null || mListProduct!!.isEmpty()) return
                for (i in mListProduct!!.indices) {
                    if (product.id == mListProduct!![i].id) {
                        mListProduct!![i] = product
                        break
                    }
                }
                if (mAdminProductAdapter != null) mAdminProductAdapter!!.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val product = dataSnapshot.getValue(
                    Product::class.java
                )
                if (product == null || mListProduct == null || mListProduct!!.isEmpty()) return
                for (productObject in mListProduct!!) {
                    if (product.id == productObject.id) {
                        mListProduct!!.remove(productObject)
                        break
                    }
                }
                if (mAdminProductAdapter != null) mAdminProductAdapter!!.notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        get(requireActivity()).productDatabaseReference.addChildEventListener(mChildEventListener!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity != null && mChildEventListener != null) {
            get(requireActivity()).productDatabaseReference
                .removeEventListener(mChildEventListener!!)
        }
    }
}
