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
import com.pro.book.activity.admin.AdminAddCategoryActivity
import com.pro.book.activity.admin.AdminProductByCategoryActivity
import com.pro.book.adapter.admin.AdminCategoryAdapter
import com.pro.book.listener.IOnAdminManagerCategoryListener
import com.pro.book.model.Category
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.getTextSearch
import com.pro.book.utils.GlobalFunction.hideSoftKeyboard
import com.pro.book.utils.GlobalFunction.startActivity
import com.pro.book.utils.StringUtil.isEmpty
import java.util.Locale

class AdminCategoryFragment : Fragment() {
    private var mView: View? = null
    private var mListCategory: MutableList<Category>? = null
    private var mAdminCategoryAdapter: AdminCategoryAdapter? = null
    private var mChildEventListener: ChildEventListener? = null
    private var edtSearchName: EditText? = null
    private var imgSearch: ImageView? = null
    private var btnAdd: FloatingActionButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_admin_category, container, false)

        initUi()
        initView()
        initListener()
        loadListCategory("")

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
        mListCategory = ArrayList()
        mAdminCategoryAdapter =
            AdminCategoryAdapter(mListCategory, object : IOnAdminManagerCategoryListener {
                override fun onClickUpdateCategory(category: Category) {
                    onClickEditCategory(category)
                }

                override fun onClickDeleteCategory(category: Category) {
                    deleteCategoryItem(category)
                }

                override fun onClickItemCategory(category: Category) {
                    goToProductOfCategory(category)
                }
            })
        rcvData.adapter = mAdminCategoryAdapter
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
        btnAdd!!.setOnClickListener { onClickAddCategory() }

        imgSearch!!.setOnClickListener { searchCategory() }

        edtSearchName!!.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchCategory()
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
                    searchCategory()
                }
            }
        })
    }

    private fun onClickAddCategory() {
        startActivity(requireActivity(), AdminAddCategoryActivity::class.java)
    }

    private fun goToProductOfCategory(category: Category) {
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_INTENT_CATEGORY_OBJECT, category)
        startActivity(requireActivity(), AdminProductByCategoryActivity::class.java, bundle)
    }

    private fun onClickEditCategory(category: Category) {
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_INTENT_CATEGORY_OBJECT, category)
        startActivity(requireActivity(), AdminAddCategoryActivity::class.java, bundle)
    }

    private fun deleteCategoryItem(category: Category) {
        AlertDialog.Builder(activity)
            .setTitle(getString(R.string.msg_delete_title))
            .setMessage(getString(R.string.msg_confirm_delete))
            .setPositiveButton(getString(R.string.action_ok)) { _: DialogInterface?, _: Int ->
                if (activity == null) {
                    return@setPositiveButton
                }
                get(requireActivity()).categoryDatabaseReference
                    .child(category.id.toString())
                    .removeValue { _: DatabaseError?, _: DatabaseReference? ->
                        Toast.makeText(
                            activity,
                            getString(R.string.msg_delete_category_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .setNegativeButton(getString(R.string.action_cancel), null)
            .show()
    }

    private fun searchCategory() {
        val strKey = edtSearchName!!.text.toString().trim { it <= ' ' }
        resetListCategory()
        if (activity != null && mChildEventListener != null) {
            get(requireActivity()).categoryDatabaseReference
                .removeEventListener(mChildEventListener!!)
        }
        loadListCategory(strKey)
        hideSoftKeyboard(requireActivity())
    }

    private fun resetListCategory() {
        if (mListCategory != null) {
            mListCategory!!.clear()
        } else {
            mListCategory = ArrayList()
        }
    }

    private fun loadListCategory(keyword: String?) {
        if (activity == null) return
        mChildEventListener = object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val category = dataSnapshot.getValue(
                    Category::class.java
                )
                if (category == null || mListCategory == null) return
                if (isEmpty(keyword)) {
                    mListCategory!!.add(0, category)
                } else {
                    if (getTextSearch(category.name).lowercase(Locale.getDefault())
                            .trim { it <= ' ' }
                            .contains(
                                getTextSearch(keyword).lowercase(Locale.getDefault())
                                    .trim { it <= ' ' })
                    ) {
                        mListCategory!!.add(0, category)
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter!!.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val category = dataSnapshot.getValue(
                    Category::class.java
                )
                if (category == null || mListCategory == null || mListCategory!!.isEmpty()) return
                for (i in mListCategory!!.indices) {
                    if (category.id == mListCategory!![i].id) {
                        mListCategory!![i] = category
                        break
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter!!.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val category = dataSnapshot.getValue(
                    Category::class.java
                )
                if (category == null || mListCategory == null || mListCategory!!.isEmpty()) return
                for (categoryObject in mListCategory!!) {
                    if (category.id == categoryObject.id) {
                        mListCategory!!.remove(categoryObject)
                        break
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter!!.notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        get(requireActivity()).categoryDatabaseReference.addChildEventListener(mChildEventListener!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity != null && mChildEventListener != null) {
            get(requireActivity()).categoryDatabaseReference
                .removeEventListener(mChildEventListener!!)
        }
    }
}
