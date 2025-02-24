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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.BaseActivity
import com.pro.book.adapter.admin.AdminVoucherAdapter
import com.pro.book.listener.IOnAdminManagerVoucherListener
import com.pro.book.model.Voucher
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.startActivity

class AdminVoucherActivity : BaseActivity() {
    private var btnAdd: FloatingActionButton? = null
    private var mListVoucher: MutableList<Voucher>? = null
    private var mAdminVoucherAdapter: AdminVoucherAdapter? = null
    private var mChildEventListener: ChildEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_voucher)

        initToolbar()
        initUi()
        initView()
        loadListVoucher()
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { onBackPressed() }
        tvToolbarTitle.text = getString(R.string.manage_voucher)
    }

    private fun initUi() {
        btnAdd = findViewById(R.id.btn_add)
        btnAdd?.setOnClickListener { onClickAddVoucher() }
    }

    private fun initView() {
        val rcvData = findViewById<RecyclerView>(R.id.rcv_data)
        val linearLayoutManager = LinearLayoutManager(this)
        rcvData.layoutManager = linearLayoutManager
        mListVoucher = ArrayList()
        mAdminVoucherAdapter =
            AdminVoucherAdapter(mListVoucher, object : IOnAdminManagerVoucherListener {
                override fun onClickUpdateVoucher(voucher: Voucher) {
                    onClickEditVoucher(voucher)
                }

                override fun onClickDeleteVoucher(voucher: Voucher) {
                    deleteVoucherItem(voucher)
                }
            })
        rcvData.adapter = mAdminVoucherAdapter
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

    private fun onClickAddVoucher() {
        startActivity(this, AdminAddVoucherActivity::class.java)
    }

    private fun onClickEditVoucher(voucher: Voucher) {
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_INTENT_VOUCHER_OBJECT, voucher)
        startActivity(this, AdminAddVoucherActivity::class.java, bundle)
    }

    private fun deleteVoucherItem(voucher: Voucher) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.msg_delete_title))
            .setMessage(getString(R.string.msg_confirm_delete))
            .setPositiveButton(getString(R.string.action_ok)) { _: DialogInterface?, _: Int ->
                get(
                    this
                ).voucherDatabaseReference
                    .child(voucher.id.toString())
                    .removeValue { _: DatabaseError?, _: DatabaseReference? ->
                        Toast.makeText(
                            this,
                            getString(R.string.msg_delete_voucher_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .setNegativeButton(getString(R.string.action_cancel), null)
            .show()
    }

    private fun loadListVoucher() {
        mChildEventListener = object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val voucher = dataSnapshot.getValue(Voucher::class.java)
                if (voucher == null || mListVoucher == null) return
                mListVoucher!!.add(0, voucher)
                if (mAdminVoucherAdapter != null) mAdminVoucherAdapter!!.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val voucher = dataSnapshot.getValue(Voucher::class.java)
                if (voucher == null || mListVoucher == null || mListVoucher!!.isEmpty()) return
                for (i in mListVoucher!!.indices) {
                    if (voucher.id == mListVoucher!![i].id) {
                        mListVoucher!![i] = voucher
                        break
                    }
                }
                if (mAdminVoucherAdapter != null) mAdminVoucherAdapter!!.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val voucher = dataSnapshot.getValue(Voucher::class.java)
                if (voucher == null || mListVoucher == null || mListVoucher!!.isEmpty()) return
                for (voucherObject in mListVoucher!!) {
                    if (voucher.id == voucherObject.id) {
                        mListVoucher!!.remove(voucherObject)
                        break
                    }
                }
                if (mAdminVoucherAdapter != null) mAdminVoucherAdapter!!.notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        get(this).voucherDatabaseReference.addChildEventListener(mChildEventListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChildEventListener != null) {
            get(this).voucherDatabaseReference.addChildEventListener(
                mChildEventListener!!
            )
        }
    }
}
