package com.pro.book.activity.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.BaseActivity
import com.pro.book.adapter.admin.AdminRoleAdapter
import com.pro.book.model.Admin
import com.pro.book.utils.GlobalFunction.startActivity

class AdminRoleActivity : BaseActivity() {
    private var btnAdd: FloatingActionButton? = null
    private var mListAdmin: MutableList<Admin>? = null
    private var mAdminRoleAdapter: AdminRoleAdapter? = null
    private var mChildEventListener: ChildEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_role)

        initToolbar()
        initUi()
        initView()
        loadListAdmin()
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { onBackPressed() }
        tvToolbarTitle.text = getString(R.string.label_role_admin)
    }

    private fun initUi() {
        btnAdd = findViewById(R.id.btn_add)
        btnAdd?.setOnClickListener { onClickAddAdmin() }
    }

    private fun initView() {
        val rcvData = findViewById<RecyclerView>(R.id.rcv_data)
        val linearLayoutManager = LinearLayoutManager(this)
        rcvData.layoutManager = linearLayoutManager
        mListAdmin = ArrayList()
        mAdminRoleAdapter = AdminRoleAdapter(mListAdmin)
        rcvData.adapter = mAdminRoleAdapter
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

    private fun onClickAddAdmin() {
        startActivity(this, AdminAddRoleActivity::class.java)
    }

    private fun loadListAdmin() {
        mChildEventListener = object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val admin = dataSnapshot.getValue(Admin::class.java)
                if (admin == null || mListAdmin == null) return
                mListAdmin!!.add(0, admin)
                if (mAdminRoleAdapter != null) mAdminRoleAdapter!!.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val admin = dataSnapshot.getValue(Admin::class.java)
                if (admin == null || mListAdmin == null || mListAdmin!!.isEmpty()) return
                for (i in mListAdmin!!.indices) {
                    if (admin.id == mListAdmin!![i].id) {
                        mListAdmin!![i] = admin
                        break
                    }
                }
                if (mAdminRoleAdapter != null) mAdminRoleAdapter!!.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val admin = dataSnapshot.getValue(Admin::class.java)
                if (admin == null || mListAdmin == null || mListAdmin!!.isEmpty()) return
                for (adminObject in mListAdmin!!) {
                    if (admin.id == adminObject.id) {
                        mListAdmin!!.remove(adminObject)
                        break
                    }
                }
                if (mAdminRoleAdapter != null) mAdminRoleAdapter!!.notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        get(this).adminDatabaseReference.addChildEventListener(mChildEventListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChildEventListener != null) {
            get(this).adminDatabaseReference.addChildEventListener(
                mChildEventListener!!
            )
        }
    }
}
